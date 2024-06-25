package com.hyx.ssl.modules.cert.enums;

import lombok.Getter;

@Getter
public enum MsgTypeEnum {
    /**
     * 消息类型
     */
    FEISHU_BOT_HOOK("飞书机器人"),
    ;

    private String info;

    MsgTypeEnum(String info) {
        this.info = info;
    }
}
