package com.hyx.ssl.modules.cert.strategy.deploy.impl;

import com.hyx.ssl.modules.ali.service.AliOssService;
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
 * 阿里云oss部署证书
 */
@Service
@AllArgsConstructor
public class AliOssDeployStrategyImpl implements DeployStrategy {
    private final AliOssService aliOssService;
    private final ICertInfoService certInfoService;
    private final IAuthConfigService authConfigService;

    @Override
    public R<Object> deploy(CertDeployEntity entity) {
        if (entity == null) {
            return R.fail("deploy对象不能为空");
        }

        AuthConfigEntity authConfig = authConfigService.getById(entity.getAuthConfigId());
        if (authConfig == null) {
            return R.fail("authConfig服务配置获取失败");
        }

        CertInfoEntity certInfo = certInfoService.getById(entity.getCertId());

        String accessKey = DbSecureUtil.decryptFromDb(authConfig.getAccessKey());
        String secretKey = DbSecureUtil.decryptFromDb(authConfig.getSecretKey());
        aliOssService.deploySSL(accessKey, secretKey,
            entity.getAliOssEndpoint(), entity.getAliOssBucket(),
            entity.getDomain(), certInfo.getPublicKey(), certInfo.getPrivateKey());
        return R.status(true);
    }
}
