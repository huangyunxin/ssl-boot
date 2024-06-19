package com.hyx.ssl.modules.cert.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyx.ssl.modules.cert.entity.CertDeployEntity;
import com.hyx.ssl.tool.api.R;

import java.util.Date;

public interface ICertDeployService extends IService<CertDeployEntity> {

    /**
     * 部署证书
     */
    void deployCert(CertDeployEntity entity);

    /**
     * 获取下次执行时间
     */
    R<Date> getNextExecuteTime(CertDeployEntity entity);
}
