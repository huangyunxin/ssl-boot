package com.hyx.ssl.modules.cert.strategy.deploy.impl;

import com.hyx.ssl.modules.cert.entity.CertDeployEntity;
import com.hyx.ssl.modules.cert.entity.CertInfoEntity;
import com.hyx.ssl.modules.cert.service.ICertInfoService;
import com.hyx.ssl.modules.cert.strategy.deploy.DeployStrategy;
import com.hyx.ssl.modules.qiniu.service.QiniuDnsService;
import com.hyx.ssl.tool.api.R;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 七牛云cdn部署证书
 */
@Service
@AllArgsConstructor
public class QiniuCdnDeployStrategyImpl implements DeployStrategy {
    private final QiniuDnsService qiniuDnsService;
    private final ICertInfoService certInfoService;

    @Override
    public R<Object> deploy(CertDeployEntity entity) throws Exception {
        if (entity == null) {
            return R.fail("deploy对象不能为空");
        }

        CertInfoEntity certInfo = certInfoService.getById(entity.getCertId());
        qiniuDnsService.deploySSL(entity.getQiniuAccessKey(), entity.getQiniuSecretKey(),
            entity.getDomain(), certInfo.getPublicKey(), certInfo.getPrivateKey());
        return R.status(true);
    }
}
