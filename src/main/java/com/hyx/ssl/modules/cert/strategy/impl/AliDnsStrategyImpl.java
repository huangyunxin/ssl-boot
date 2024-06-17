package com.hyx.ssl.modules.cert.strategy.impl;

import com.hyx.ssl.modules.ali.service.AliDnsService;
import com.hyx.ssl.modules.cert.entity.CertInfoEntity;
import com.hyx.ssl.modules.cert.strategy.DnsStrategy;
import com.hyx.ssl.tool.api.R;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 赠送会员天数
 */
@Service
@AllArgsConstructor
public class AliDnsStrategyImpl implements DnsStrategy {
    private final AliDnsService aliDnsService;

    @Override
    public R<Object> addDomainRecord(CertInfoEntity entity, String domain, String rrKeyWord, String typeKeyWord, String value) {
        try {
            aliDnsService.addDomainRecord(entity.getAccountAccessKeyId(), entity.getAccountAccessKeySecret(),
                domain, rrKeyWord, typeKeyWord, value);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail(e.getMessage());
        }
        return R.status(true);
    }
}
