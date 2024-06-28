package com.hyx.ssl.modules.cert.enums;

import lombok.Getter;

@Getter
public enum DomainRecordTypeEnum {
    /**
     * 域名解析类型
     */
    TXT("文本解析"),
    ;

    private String info;

    DomainRecordTypeEnum(String info) {
        this.info = info;
    }
}
