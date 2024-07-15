package com.hyx.ssl.modules.cert.strategy.dns.impl;

import com.hyx.ssl.modules.auth.entity.AuthConfigEntity;
import com.hyx.ssl.modules.auth.service.IAuthConfigService;
import com.hyx.ssl.modules.cert.entity.CertInfoEntity;
import com.hyx.ssl.modules.cert.enums.DomainRecordTypeEnum;
import com.hyx.ssl.modules.cert.strategy.dns.DnsStrategy;
import com.hyx.ssl.modules.tencent.service.TencentDnsService;
import com.hyx.ssl.tool.api.R;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 腾讯云dns解析
 */
@Service
@AllArgsConstructor
public class TencentDnsStrategyImpl implements DnsStrategy {
    private final TencentDnsService tencentDnsService;
    private final IAuthConfigService authConfigService;

    @Override
    public R<Object> addDomainRecord(CertInfoEntity entity, String domain, String domainPrefix,
                                     DomainRecordTypeEnum type, String value) {
        try {
            AuthConfigEntity authConfig = authConfigService.getById(entity.getAuthConfigId());
            if (authConfig == null) {
                return R.fail("authConfig服务配置获取失败");
            }

            return tencentDnsService.addDomainRecord(authConfig.getAccessKey(), authConfig.getSecretKey(),
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

            return tencentDnsService.deleteDomainRecord(authConfig.getAccessKey(), authConfig.getSecretKey(),
                domain, domainPrefix);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail(e.getMessage());
        }
    }
}
