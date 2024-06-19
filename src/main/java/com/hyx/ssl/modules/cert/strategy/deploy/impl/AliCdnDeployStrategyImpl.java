package com.hyx.ssl.modules.cert.strategy.deploy.impl;

import com.hyx.ssl.modules.ali.service.AliDnsService;
import com.hyx.ssl.modules.cert.entity.CertDeployEntity;
import com.hyx.ssl.modules.cert.strategy.deploy.DeployStrategy;
import com.hyx.ssl.tool.api.R;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 阿里云cdn服务
 */
@Service
@AllArgsConstructor
public class AliCdnDeployStrategyImpl implements DeployStrategy {
    private final AliDnsService aliDnsService;

    @Override
    public R<Object> deploy(CertDeployEntity entity) {
        return R.status(true);
    }
}
