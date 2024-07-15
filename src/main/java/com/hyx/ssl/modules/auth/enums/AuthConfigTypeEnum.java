package com.hyx.ssl.modules.auth.enums;

import lombok.Getter;

@Getter
public enum AuthConfigTypeEnum {
    /**
     * 配置类型
     */
    ALI("阿里云"),
    TENCENT("腾讯云"),
    QINIU("七牛云"),
    SERVER_SSH("远程服务器SSH"),
    FEISHU_BOT_HOOK("飞书机器人"),
    ;

    private String info;

    AuthConfigTypeEnum(String info) {
        this.info = info;
    }
}
