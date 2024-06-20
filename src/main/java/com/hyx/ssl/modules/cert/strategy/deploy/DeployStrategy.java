package com.hyx.ssl.modules.cert.strategy.deploy;

import com.hyx.ssl.modules.cert.entity.CertDeployEntity;
import com.hyx.ssl.tool.api.R;

public interface DeployStrategy {

    /**
     * 部署证书
     */
    R<Object> deploy(CertDeployEntity entity) throws Exception;
}
