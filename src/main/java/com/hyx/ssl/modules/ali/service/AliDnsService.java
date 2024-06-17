package com.hyx.ssl.modules.ali.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.alidns20150109.Client;
import com.aliyun.alidns20150109.models.*;
import com.aliyun.teautil.models.RuntimeOptions;
import com.hyx.ssl.tool.api.R;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云dns服务
 */
@Service
public class AliDnsService {
    // 缓存client
    private Map<String, Client> clientMap = new HashMap<>();

    public Client getClient(String accessKeyId, String accessKeySecret) throws Exception {
        if (!StrUtil.isAllNotBlank(accessKeyId, accessKeySecret)) {
            return null;
        }
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
            .setAccessKeyId(accessKeyId)
            .setAccessKeySecret(accessKeySecret);
        Client client = new Client(config);
        clientMap.put(accessKeyId, client);
        return client;
    }

    /**
     * 添加域名解析
     */
    public R<Object> addDomainRecord(String accessKeyId, String accessKeySecret, String domain, String rrKeyWord,
                                     String typeKeyWord, String value) throws Exception {
        Client client = this.getClient(accessKeyId, accessKeySecret);
        DescribeDomainRecordsRequest request = new com.aliyun.alidns20150109.models.DescribeDomainRecordsRequest()
            .setPageNumber(1L)
            .setPageSize(10L)
            .setDomainName(domain)
            //精确查询
            .setSearchMode("EXACT")
            .setKeyWord(rrKeyWord);
        DescribeDomainRecordsResponse describeDomainRecordsResponse = client.describeDomainRecords(request);
        DescribeDomainRecordsResponseBody body = describeDomainRecordsResponse.getBody();
        //先查询解析是否已存在
        DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecords domainRecords = body.getDomainRecords();
        if (CollUtil.isEmpty(domainRecords.getRecord())) {
            //新增解析
            AddDomainRecordRequest addDomainRecordRequest = new AddDomainRecordRequest()
                .setDomainName(domain)
                .setRR(rrKeyWord)
                .setType(typeKeyWord)
                .setValue(value);
            RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            client.addDomainRecordWithOptions(addDomainRecordRequest, runtime);
        } else {
            String recordId = domainRecords.getRecord().get(0).getRecordId();
            if (!typeKeyWord.equals(domainRecords.getRecord().get(0).getType()) ||
                !value.equals(domainRecords.getRecord().get(0).getValue())) {
                //修改解析
                UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest()
                    .setRecordId(recordId)
                    .setRR(rrKeyWord)
                    .setType(typeKeyWord)
                    .setValue(value);
                RuntimeOptions runtime = new RuntimeOptions();
                client.updateDomainRecordWithOptions(updateDomainRecordRequest, runtime);
            }
        }
        return R.status(true);
    }
}
