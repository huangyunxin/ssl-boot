package com.hyx.ssl.modules.cert.strategy.dns;

import com.hyx.ssl.modules.cert.enums.DomainDnsTypeEnum;
import com.hyx.ssl.modules.cert.strategy.dns.impl.AliDnsStrategyImpl;
import com.hyx.ssl.modules.cert.strategy.dns.impl.TencentDnsStrategyImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DnsStrategyFactory {
    private final AliDnsStrategyImpl aliDnsStrategy;
    private final TencentDnsStrategyImpl tencentDnsStrategy;

    public DnsStrategy getCardStrategy(String type) {
        if (DomainDnsTypeEnum.ALI_AUTHORITY.name().equals(type)) {
            return aliDnsStrategy;
        } else if (DomainDnsTypeEnum.TENCENT_DNS.name().equals(type)) {
            return tencentDnsStrategy;
        }
        throw new RuntimeException("dns类型不可用");
    }
}
