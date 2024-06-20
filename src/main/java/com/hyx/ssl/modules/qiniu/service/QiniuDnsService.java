package com.hyx.ssl.modules.qiniu.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hyx.ssl.tool.api.R;
import com.qiniu.common.Constants;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.util.Auth;
import com.qiniu.util.Json;
import com.qiniu.util.StringMap;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

/**
 * 七牛云dns服务
 */
@Service
public class QiniuDnsService {

    /**
     * 获取域名信息
     */
    public R<JSONObject> getDomainInfo(String accessKey, String secretKey, String domain) throws Exception {
        if (!StrUtil.isAllNotBlank(accessKey, secretKey, domain)) {
            return R.fail("获取域名信息：参数不能为空");
        }

        String url = "https://api.qiniu.com/domain/" + StrUtil.trim(domain);
        Auth auth = Auth.create(accessKey, secretKey);
        StringMap headers = auth.authorizationV2(url, "GET", null, null);

        Client client = new Client();
        Response response = client.get(url, headers);
        String resStr = response.bodyString();
        if (StrUtil.isBlank(resStr)) {
            return R.fail("获取域名信息失败，结果为空");
        }
        JSONObject resObj = JSONUtil.parseObj(resStr);
        return R.data(resObj);
    }


    /**
     * 开启https
     */
    public R<JSONObject> enabledHttps(String accessKey, String secretKey, String domain, String certId,
                                      Boolean forceHttps, Boolean http2Enable) throws Exception {
        if (!StrUtil.isAllNotBlank(accessKey, secretKey, domain, certId)) {
            return R.fail("修改域名证书：参数不能为空");
        }

        HashMap<String, Object> req = new HashMap<>();
        req.put("certId", certId);
        req.put("forceHttps", forceHttps);
        req.put("http2Enable", http2Enable);

        byte[] body = Json.encode(req).getBytes(Constants.UTF_8);
        String url = "https://api.qiniu.com/domain/" + StrUtil.trim(domain) + "/sslize";
        Auth auth = Auth.create(accessKey, secretKey);
        StringMap headers = auth.authorizationV2(url, "PUT", body, Client.JsonMime);

        Client client = new Client();
        Response response = client.put(url, body, headers, Client.JsonMime);
        if (response.isOK()) {
            return R.status(true);
        } else {
            return R.fail("开启https失败，" + response.error);
        }
    }

    /**
     * 上传证书
     */
    public R<JSONObject> uploadCert(String accessKey, String secretKey,
                                    String certPublicKey, String certPrivateKey) throws Exception {
        if (!StrUtil.isAllNotBlank(accessKey, secretKey, certPublicKey, certPrivateKey)) {
            return R.fail("上传证书失败，参数不能为空");
        }

        HashMap<String, String> req = new HashMap<>();
        req.put("name", "cert_" + DateUtil.format(new Date(), "yyyyMMdd_HHmmss") + "_" + RandomUtil.randomString(4));
        req.put("pri", StrUtil.trim(certPrivateKey));
        req.put("ca", StrUtil.trim(certPublicKey));

        byte[] body = Json.encode(req).getBytes(Constants.UTF_8);
        String url = "https://api.qiniu.com/sslcert";
        Auth auth = Auth.create(accessKey, secretKey);
        StringMap headers = auth.authorizationV2(url, "POST", body, Client.JsonMime);

        Client client = new Client();
        Response response = client.post(url, body, headers, Client.JsonMime);
        String resStr = response.bodyString();
        if (StrUtil.isBlank(resStr)) {
            return R.fail("上传证书失败，上传结果为空");
        }
        JSONObject resObj = JSONUtil.parseObj(resStr);
        Integer code = resObj.getInt("code");
        String certId = resObj.getStr("certID");
        if (200 == code && StrUtil.isNotBlank(certId)) {
            return R.data(new JSONObject().set("certId", certId));
        } else {
            return R.fail("上传证书失败，" + resObj.getStr("error"));
        }
    }

    /**
     * 修改域名证书
     */
    public R<JSONObject> updateDomainCert(String accessKey, String secretKey, String domain, String certId,
                                          Boolean forceHttps, Boolean http2Enable) throws Exception {
        if (!StrUtil.isAllNotBlank(accessKey, secretKey, domain, certId)) {
            return R.fail("修改域名证书：参数不能为空");
        }

        HashMap<String, Object> req = new HashMap<>();
        req.put("certId", certId);
        req.put("forceHttps", forceHttps);
        req.put("http2Enable", http2Enable);

        byte[] body = Json.encode(req).getBytes(Constants.UTF_8);
        String url = "https://api.qiniu.com/domain/" + StrUtil.trim(domain) + "/httpsconf";
        Auth auth = Auth.create(accessKey, secretKey);
        StringMap headers = auth.authorizationV2(url, "PUT", body, Client.JsonMime);

        Client client = new Client();
        Response response = client.put(url, body, headers, Client.JsonMime);
        String resStr = response.bodyString();
        if (StrUtil.isBlank(resStr)) {
            return R.fail("修改证书失败，结果为空");
        }
        JSONObject resObj = JSONUtil.parseObj(resStr);
        Integer code = resObj.getInt("code");
        if (200 == code) {
            return R.status(true);
        } else {
            return R.fail("修改证书失败，" + resObj.getStr("error"));
        }
    }

    /**
     * 部署证书
     */
    public void deploySSL(String accessKey, String secretKey, String domain,
                          String certPublicKey, String certPrivateKey) throws Exception {
        //上传证书
        R<JSONObject> uploadCertRes = this.uploadCert(accessKey, secretKey, certPublicKey, certPrivateKey);
        String certId = uploadCertRes.getData() != null ? uploadCertRes.getData().getStr("certId") : null;
        if (StrUtil.isBlank(certId)) {
            throw new Exception(uploadCertRes.getMsg());
        }

        //获取域名信息
        R<JSONObject> domainInfoRes = this.getDomainInfo(accessKey, secretKey, domain);
        JSONObject httpsObj = domainInfoRes.getData().getJSONObject("https");
        String oldCertId = httpsObj.getStr("certId");
        Boolean forceHttps = httpsObj.getBool("forceHttps");
        Boolean http2Enable = httpsObj.getBool("http2Enable");

        //未开启https
        if (StrUtil.isBlank(oldCertId)) {
            //开启https
            R<JSONObject> enabledHttpsRes = this.enabledHttps(accessKey, secretKey, domain,
                certId, false, false);
            if (!enabledHttpsRes.isSuccess()) {
                throw new Exception(enabledHttpsRes.getMsg());
            }
        } else {
            //修改证书
            R<JSONObject> updateDomainCertRes = this.updateDomainCert(accessKey, secretKey, domain,
                certId, forceHttps, http2Enable);
            if (!updateDomainCertRes.isSuccess()) {
                throw new Exception(updateDomainCertRes.getMsg());
            }
        }
    }
}
