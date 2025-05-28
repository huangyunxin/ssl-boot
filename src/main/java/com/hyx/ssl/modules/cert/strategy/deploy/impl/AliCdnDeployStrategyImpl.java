package com.hyx.ssl.modules.cert.strategy.deploy.impl;

import com.hyx.ssl.modules.ali.service.AliCdnService;
import com.hyx.ssl.modules.auth.entity.AuthConfigEntity;
import com.hyx.ssl.modules.auth.service.IAuthConfigService;
import com.hyx.ssl.modules.cert.entity.CertDeployEntity;
import com.hyx.ssl.modules.cert.entity.CertInfoEntity;
import com.hyx.ssl.modules.cert.service.ICertInfoService;
import com.hyx.ssl.modules.cert.strategy.deploy.DeployStrategy;
import com.hyx.ssl.tool.api.R;
import com.hyx.ssl.util.DbSecureUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 阿里云cdn部署证书
 */
@Service
@AllArgsConstructor
public class AliCdnDeployStrategyImpl implements DeployStrategy {
    private final AliCdnService aliCdnService;
    private final ICertInfoService certInfoService;
    private final IAuthConfigService authConfigService;

    @Override
    public R<Object> deploy(CertDeployEntity entity) throws Exception {
        if (entity == null) {
            return R.fail("deploy对象不能为空");
        }

        AuthConfigEntity authConfig = authConfigService.getById(entity.getAuthConfigId());
        if (authConfig == null) {
            return R.fail("authConfig服务配置获取失败");
        }

        String accessKey = DbSecureUtil.decryptFromDb(authConfig.getAccessKey());
        String secretKey = DbSecureUtil.decryptFromDb(authConfig.getSecretKey());
        CertInfoEntity certInfo = certInfoService.getById(entity.getCertId());
        aliCdnService.deploySSL(accessKey, secretKey, entity.getAliCdnEndpoint(),
            entity.getDomain(), certInfo.getPublicKey(), certInfo.getPrivateKey());
        return R.status(true);
    }
}
