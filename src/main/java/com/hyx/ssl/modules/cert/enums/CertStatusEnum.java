package com.hyx.ssl.modules.cert.enums;

import lombok.Getter;

@Getter
public enum CertStatusEnum {
    /**
     * 证书状态
     */
    UNDERWAY("进行中"),
    DONE("已完成"),
    FAIL("失败");

    private String info;

    CertStatusEnum(String info) {
        this.info = info;
    }
}
