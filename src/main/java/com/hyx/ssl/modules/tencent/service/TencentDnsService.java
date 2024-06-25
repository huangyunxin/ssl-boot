package com.hyx.ssl.modules.tencent.service;

import cn.hutool.core.util.ArrayUtil;
import com.hyx.ssl.tool.api.R;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.dnspod.v20210323.DnspodClient;
import com.tencentcloudapi.dnspod.v20210323.models.*;
import org.springframework.stereotype.Service;

/**
 * 腾讯云dns服务
 */
@Service
public class TencentDnsService {

    /**
     * 添加域名解析
     */
    public R<Object> addDomainRecord(String secretId, String secretKey, String domain, String subdomain,
                                     String type, String value) throws Exception {

        String recordLine = "默认";

        Credential cred = new Credential(secretId, secretKey);
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("dnspod.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        DnspodClient client = new DnspodClient(cred, "", clientProfile);
        //查询记录
        RecordListItem oldRecord = null;
        try {
            DescribeRecordListRequest searchReq = new DescribeRecordListRequest();
            searchReq.setDomain(domain);
            searchReq.setSubdomain(subdomain);
            DescribeRecordListResponse searchResp = client.DescribeRecordList(searchReq);
            oldRecord = ArrayUtil.get(searchResp.getRecordList(), 0);
        } catch (Exception e) {
        }

        if (oldRecord == null) {
            //新增解析
            CreateRecordRequest req = new CreateRecordRequest();
            req.setDomain(domain);
            req.setSubDomain(subdomain);
            req.setRecordType(type);
            req.setRecordLine(recordLine);
            req.setValue(value);
            client.CreateRecord(req);
        } else {
            Long recordId = oldRecord.getRecordId();
            if (!type.equals(oldRecord.getType()) ||
                !value.equals(oldRecord.getValue())) {
                //修改解析
                ModifyRecordRequest req = new ModifyRecordRequest();
                req.setDomain(domain);
                req.setSubDomain(subdomain);
                req.setRecordType(type);
                req.setRecordLine(recordLine);
                req.setValue(value);
                req.setRecordId(recordId);
                client.ModifyRecord(req);
            }
        }
        return R.status(true);
    }
}
