package com.hyx.ssl.modules.ali.service;

import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.AddBucketCnameRequest;
import com.aliyun.oss.model.CertificateConfiguration;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云oss服务
 */
@Service
public class AliOssService {
    // 缓存client
    private Map<String, OSS> clientMap = new HashMap<>();

    public OSS getClient(String accessKeyId, String accessKeySecret, String endpoint) {
        if (!StrUtil.isAllNotBlank(accessKeyId, accessKeySecret, endpoint)) {
            return null;
        }

        String mapKey = accessKeyId + "_" + accessKeySecret + "_" + endpoint;

        OSS client = clientMap.get(mapKey);
        if (client != null) {
            return client;
        }

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        clientMap.put(mapKey, ossClient);
        return ossClient;
    }

    /**
     * 部署证书
     */
    public void deploySSL(String accessKeyId, String accessKeySecret, String endpoint, String bucketName,
                          String domain, String certPublicKey, String certPrivateKey) {

        OSS client = this.getClient(accessKeyId, accessKeySecret, endpoint);

        // 添加CNAME记录。
        AddBucketCnameRequest request = new AddBucketCnameRequest(bucketName)
            .withDomain(domain)
            .withCertificateConfiguration(new CertificateConfiguration()
                // 设置证书公钥。
                .withPublicKey(certPublicKey)
                // 设置证书私钥。
                .withPrivateKey(certPrivateKey)
                // 是否强制覆盖证书。
                .withForceOverwriteCert(true)
                // 是否删除证书。
                .withDeleteCertificate(false));
        client.addBucketCname(request);
    }
}
