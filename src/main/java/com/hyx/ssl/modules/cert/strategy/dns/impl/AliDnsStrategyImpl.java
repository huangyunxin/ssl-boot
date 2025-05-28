package com.hyx.ssl.modules.cert.strategy.dns.impl;

import com.hyx.ssl.modules.ali.service.AliDnsService;
import com.hyx.ssl.modules.auth.entity.AuthConfigEntity;
import com.hyx.ssl.modules.auth.service.IAuthConfigService;
import com.hyx.ssl.modules.cert.entity.CertInfoEntity;
import com.hyx.ssl.modules.cert.enums.DomainRecordTypeEnum;
import com.hyx.ssl.modules.cert.strategy.dns.DnsStrategy;
import com.hyx.ssl.tool.api.R;
import com.hyx.ssl.util.DbSecureUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 阿里云dns解析
 */
@Service
@AllArgsConstructor
public class AliDnsStrategyImpl implements DnsStrategy {
    private final AliDnsService aliDnsService;
    private final IAuthConfigService authConfigService;

    @Override
    public R<Object> addDomainRecord(CertInfoEntity entity, String domain, String domainPrefix,
                                     DomainRecordTypeEnum type, String value) {
        try {
            AuthConfigEntity authConfig = authConfigService.getById(entity.getAuthConfigId());
            if (authConfig == null) {
                return R.fail("authConfig服务配置获取失败");
            }

            String accessKey = DbSecureUtil.decryptFromDb(authConfig.getAccessKey());
            String secretKey = DbSecureUtil.decryptFromDb(authConfig.getSecretKey());
            return aliDnsService.addDomainRecord(accessKey, secretKey,
                domain, domainPrefix, type.name(), value);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail(e.getMessage());
        }
    }

    @Override
    public R<Object> deleteDomainRecord(CertInfoEntity entity, String domain, String domainPrefix) {
        try {
            AuthConfigEntity authConfig = authConfigService.getById(entity.getAuthConfigId());
            if (authConfig == null) {
                return R.fail("authConfig服务配置获取失败");
            }

            String accessKey = DbSecureUtil.decryptFromDb(authConfig.getAccessKey());
            String secretKey = DbSecureUtil.decryptFromDb(authConfig.getSecretKey());
            return aliDnsService.deleteDomainRecord(accessKey, secretKey,
                domain, domainPrefix);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail(e.getMessage());
        }
    }
}
