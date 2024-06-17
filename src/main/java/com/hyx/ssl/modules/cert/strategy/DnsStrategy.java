package com.hyx.ssl.modules.cert.strategy;

import com.hyx.ssl.modules.cert.entity.CertInfoEntity;
import com.hyx.ssl.tool.api.R;

public interface DnsStrategy {

    /**
     * 添加dns解析
     */
    R<Object> addDomainRecord(CertInfoEntity entity, String domain, String rrKeyWord,
                              String typeKeyWord, String value);
}
