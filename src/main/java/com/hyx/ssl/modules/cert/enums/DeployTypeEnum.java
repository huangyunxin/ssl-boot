package com.hyx.ssl.modules.cert.enums;

import lombok.Getter;

@Getter
public enum DeployTypeEnum {
    /**
     * 部署类型
     */
    ALI_OSS("阿里云-对象存储"),
    ALI_CDN("阿里云-CDN"),
    QINIU_CDN("七牛-CDN"),
    ;

    private String info;

    DeployTypeEnum(String info) {
        this.info = info;
    }
}
