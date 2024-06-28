package com.hyx.ssl.modules.cert.strategy.dns;

import com.hyx.ssl.modules.cert.entity.CertInfoEntity;
import com.hyx.ssl.modules.cert.enums.DomainRecordTypeEnum;
import com.hyx.ssl.tool.api.R;

public interface DnsStrategy {

    /**
     * 添加域名解析
     *
     * @param entity       证书信息类
     * @param domain       域名（如：example.com）
     * @param domainPrefix 主机记录前缀 (如：www)
     * @param type         域名解析类型
     * @param value        域名解析值
     */
    R<Object> addDomainRecord(CertInfoEntity entity, String domain, String domainPrefix,
                              DomainRecordTypeEnum type, String value);

    /**
     * 删除域名解析
     */
    R<Object> deleteDomainRecord(CertInfoEntity entity, String domain, String domainPrefix);
}
