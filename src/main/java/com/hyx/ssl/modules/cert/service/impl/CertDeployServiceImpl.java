package com.hyx.ssl.modules.cert.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyx.ssl.modules.cert.entity.CertDeployEntity;
import com.hyx.ssl.modules.cert.entity.CertInfoEntity;
import com.hyx.ssl.modules.cert.enums.CertStatusEnum;
import com.hyx.ssl.modules.cert.mapper.CertDeployMapper;
import com.hyx.ssl.modules.cert.service.ICertDeployService;
import com.hyx.ssl.modules.cert.service.ICertInfoService;
import com.hyx.ssl.modules.cert.strategy.deploy.DeployStrategyFactory;
import com.hyx.ssl.tool.api.R;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

@Service
@AllArgsConstructor
public class CertDeployServiceImpl extends ServiceImpl<CertDeployMapper, CertDeployEntity> implements ICertDeployService {
    private final DeployStrategyFactory deployStrategyFactory;
    private final ICertInfoService certInfoService;

    @Override
    public void deployCert(CertDeployEntity entity) {
        StringBuffer log = new StringBuffer(StrUtil.format("==========部署开始 {}==========", DateUtil.formatDateTime(new Date())));
        CertInfoEntity certInfo = null;
        try {
            if (entity == null) {
                throw new RuntimeException("部署失败：数据不存在");
            }

            certInfo = certInfoService.getById(entity.getCertId());
            if (certInfo == null) {
                throw new RuntimeException("部署失败：证书信息不存在");
            }

            if (!StrUtil.isAllNotBlank(certInfo.getPublicKey(), certInfo.getPrivateKey())) {
                throw new RuntimeException("部署失败：证书秘钥未生成");
            }

            if (certInfo.getValidityDateEnd() == null || certInfo.getValidityDateEnd().before(new Date())) {
                throw new RuntimeException("部署失败：证书已失效");
            }

            //重置数据
            this.update(Wrappers.lambdaUpdate(CertDeployEntity.class)
                .set(CertDeployEntity::getStatus, CertStatusEnum.UNDERWAY.name())
                .set(CertDeployEntity::getLastExecuteTime, new Date())
                .set(CertDeployEntity::getLog, null)
                .set(CertDeployEntity::getCertPublicKey, null)
                .set(CertDeployEntity::getCertPrivateKey, null)
                .set(CertDeployEntity::getCertValidityDateStart, null)
                .set(CertDeployEntity::getCertValidityDateEnd, null)
                .eq(CertDeployEntity::getId, entity.getId()));

            //重新查询entity
            entity = this.getById(entity.getId());

            R<Object> deployR = deployStrategyFactory.getCardStrategy(entity.getType()).deploy(entity);
            if (!deployR.isSuccess()) {
                throw new RuntimeException(deployR.getMsg());
            }

            entity.setStatus(CertStatusEnum.DONE.name());
            log.append(StrUtil.format("\n==========部署完成 {}==========", DateUtil.formatDateTime(new Date())));
        } catch (Exception e) {
            entity.setStatus(CertStatusEnum.FAIL.name());
            try (StringWriter stringWriter = new StringWriter();
                 PrintWriter printWriter = new PrintWriter(stringWriter)) {
                e.printStackTrace(printWriter);
                log.append(StrUtil.format("\n部署失败：{}", stringWriter.toString()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            if (CertStatusEnum.DONE.name().equals(entity.getStatus())) {
                entity.setCertPublicKey(certInfo != null ? certInfo.getPublicKey() : null);
                entity.setCertPrivateKey(certInfo != null ? certInfo.getPrivateKey() : null);
                entity.setCertValidityDateStart(certInfo != null ? certInfo.getValidityDateStart() : null);
                entity.setCertValidityDateEnd(certInfo != null ? certInfo.getValidityDateEnd() : null);
            }
            entity.setLog(log.toString());
            this.updateById(entity);
        }
    }
}
