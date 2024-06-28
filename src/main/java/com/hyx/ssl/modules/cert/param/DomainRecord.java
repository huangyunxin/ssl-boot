package com.hyx.ssl.modules.cert.param;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 域名解析记录参数
 */
@Data
@AllArgsConstructor
public class DomainRecord {

    /**
     * 域名
     */
    private String domain;

    /**
     * 主机记录前缀
     */
    private String domainPrefix;
}
