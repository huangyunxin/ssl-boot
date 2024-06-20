package com.hyx.ssl.modules.ali.service;

import cn.hutool.core.util.StrUtil;
import com.aliyun.cdn20180510.Client;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云oss服务
 */
@Service
public class AliCdnService {
    // 缓存client
    private Map<String, Client> clientMap = new HashMap<>();

    public Client getClient(String accessKeyId, String accessKeySecret, String endpoint) throws Exception {
        if (!StrUtil.isAllNotBlank(accessKeyId, accessKeySecret, endpoint)) {
            return null;
        }

        String mapKey = accessKeyId + "_" + accessKeySecret;

        Client client = clientMap.get(mapKey);
        if (client != null) {
            return client;
        }

        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
            .setAccessKeyId(accessKeyId)
            .setAccessKeySecret(accessKeySecret);
        // Endpoint 请参考 https://api.aliyun.com/product/Cdn
        config.endpoint = endpoint;
        client = new Client(config);
        clientMap.put(mapKey, client);
        return client;
    }

    /**
     * 部署证书
     */
    public void deploySSL(String accessKeyId, String accessKeySecret, String endpoint,
                          String domain, String certPublicKey, String certPrivateKey) throws Exception {

        com.aliyun.cdn20180510.Client client = this.getClient(accessKeyId, accessKeySecret, endpoint);
        com.aliyun.cdn20180510.models.SetCdnDomainSSLCertificateRequest setCdnDomainSSLCertificateRequest = new com.aliyun.cdn20180510.models.SetCdnDomainSSLCertificateRequest()
            .setDomainName(domain)
            .setCertType("upload")
            .setSSLProtocol("on")
            .setSSLPub(certPublicKey)
            .setSSLPri(certPrivateKey);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        client.setCdnDomainSSLCertificateWithOptions(
            setCdnDomainSSLCertificateRequest, runtime);
    }
}
