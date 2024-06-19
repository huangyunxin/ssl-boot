package com.hyx.ssl.modules.cert.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyx.ssl.modules.cert.entity.CertDeployEntity;

public interface ICertDeployService extends IService<CertDeployEntity> {

    /**
     * 部署证书
     */
    void deployCert(CertDeployEntity entity);
}
