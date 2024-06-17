package com.hyx.ssl.modules.cert.enums;

import lombok.Getter;

@Getter
public enum DomainDnsTypeEnum {
    /**
     * 域名解析类型
     */
    ALI_AUTHORITY("阿里云-权威解析");

    private String info;

    DomainDnsTypeEnum(String info) {
        this.info = info;
    }
}
