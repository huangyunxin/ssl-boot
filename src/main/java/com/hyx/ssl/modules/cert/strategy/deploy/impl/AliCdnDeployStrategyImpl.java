package com.hyx.ssl.modules.cert.strategy.deploy.impl;

import com.hyx.ssl.modules.ali.service.AliCdnService;
import com.hyx.ssl.modules.cert.entity.CertDeployEntity;
import com.hyx.ssl.modules.cert.entity.CertInfoEntity;
import com.hyx.ssl.modules.cert.service.ICertInfoService;
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
    private final AliCdnService aliCdnService;
    private final ICertInfoService certInfoService;

    @Override
    public R<Object> deploy(CertDeployEntity entity) {
        if (entity == null) {
            return R.fail("deploy对象不能为空");
        }

        CertInfoEntity certInfo = certInfoService.getById(entity.getCertId());
        aliCdnService.deploySSL(entity.getAliAccessKeyId(), entity.getAliAccessKeySecret(), entity.getAliCdnEndpoint(),
            entity.getDomain(), certInfo.getPublicKey(), certInfo.getPrivateKey());
        return R.status(true);
    }
}
