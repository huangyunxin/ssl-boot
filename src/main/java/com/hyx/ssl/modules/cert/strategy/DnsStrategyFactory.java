package com.hyx.ssl.modules.cert.strategy;

import com.hyx.ssl.modules.cert.enums.DomainDnsTypeEnum;
import com.hyx.ssl.modules.cert.strategy.impl.AliDnsStrategyImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DnsStrategyFactory {
    private final AliDnsStrategyImpl aliDnsStrategy;

    public DnsStrategy getCardStrategy(String type) {
        if (DomainDnsTypeEnum.ALI_AUTHORITY.name().equals(type)) {
            return aliDnsStrategy;
        }
        throw new RuntimeException("dns类型不可用");
    }
}
