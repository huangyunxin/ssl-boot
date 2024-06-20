package com.hyx.ssl.modules.cert.strategy.deploy;

import com.hyx.ssl.modules.cert.enums.DeployTypeEnum;
import com.hyx.ssl.modules.cert.strategy.deploy.impl.AliCdnDeployStrategyImpl;
import com.hyx.ssl.modules.cert.strategy.deploy.impl.AliOssDeployStrategyImpl;
import com.hyx.ssl.modules.cert.strategy.deploy.impl.QiniuCdnDeployStrategyImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeployStrategyFactory {
    private final AliOssDeployStrategyImpl aliOssDeployStrategy;
    private final AliCdnDeployStrategyImpl aliCdnDeployStrategy;
    private final QiniuCdnDeployStrategyImpl qiniuCdnDeployStrategy;

    public DeployStrategy getCardStrategy(String type) {
        if (DeployTypeEnum.ALI_OSS.name().equals(type)) {
            return aliOssDeployStrategy;
        } else if (DeployTypeEnum.ALI_CDN.name().equals(type)) {
            return aliCdnDeployStrategy;
        } else if (DeployTypeEnum.QINIU_CDN.name().equals(type)) {
            return qiniuCdnDeployStrategy;
        }

        throw new RuntimeException("deploy类型不可用");
    }
}
