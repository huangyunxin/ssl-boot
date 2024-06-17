package com.hyx.ssl.modules.cert.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyx.ssl.modules.cert.entity.CertInfoEntity;

public interface ICertInfoService extends IService<CertInfoEntity> {

    /**
     * 更新证书
     */
    void updateCert(CertInfoEntity entity);

    /**
     * 异步更新证书
     */
    void updateCertAsync(CertInfoEntity entity);
}
