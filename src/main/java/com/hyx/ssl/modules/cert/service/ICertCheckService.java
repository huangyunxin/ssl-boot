package com.hyx.ssl.modules.cert.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyx.ssl.modules.cert.entity.CertCheckEntity;

public interface ICertCheckService extends IService<CertCheckEntity> {

    /**
     * 更新并发送消息
     */
    void updateAndSendMsg(CertCheckEntity entity);
}
