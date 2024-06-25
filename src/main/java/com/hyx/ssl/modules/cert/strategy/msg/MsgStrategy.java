package com.hyx.ssl.modules.cert.strategy.msg;

import com.hyx.ssl.modules.cert.entity.CertCheckEntity;
import com.hyx.ssl.tool.api.R;

public interface MsgStrategy {

    /**
     * 发送消息
     */
    R<Object> sendMsg(CertCheckEntity entity, String msg);
}
