package com.hyx.ssl.modules.cert.strategy.dns.impl;

import com.hyx.ssl.modules.ali.service.AliDnsService;
import com.hyx.ssl.modules.cert.entity.CertInfoEntity;
import com.hyx.ssl.modules.cert.strategy.dns.DnsStrategy;
import com.hyx.ssl.tool.api.R;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 阿里云dns解析
 */
@Service
@AllArgsConstructor
public class AliDnsStrategyImpl implements DnsStrategy {
    private final AliDnsService aliDnsService;

    @Override
    public R<Object> addDomainRecord(CertInfoEntity entity, String domain, String rrKeyWord, String typeKeyWord, String value) {
        try {
            aliDnsService.addDomainRecord(entity.getAliAccountAccessKeyId(), entity.getAliAccountAccessKeySecret(),
                domain, rrKeyWord, typeKeyWord, value);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail(e.getMessage());
        }
        return R.status(true);
    }
}
