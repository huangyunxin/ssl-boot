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
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
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
                throw new Exception("部署失败：数据不存在");
            }

            certInfo = certInfoService.getById(entity.getCertId());
            if (certInfo == null) {
                throw new Exception("部署失败：证书信息不存在");
            }

            if (!StrUtil.isAllNotBlank(certInfo.getPublicKey(), certInfo.getPrivateKey())) {
                throw new Exception("部署失败：证书秘钥未生成");
            }

            if (certInfo.getValidityDateEnd() == null || certInfo.getValidityDateEnd().before(new Date())) {
                throw new Exception("部署失败：证书已失效");
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
            log.append(StrUtil.format("\n部署结果：{}", deployR.getMsg()));
            if (!deployR.isSuccess()) {
                throw new Exception(deployR.getMsg());
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

    @Override
    public R<Date> getNextExecuteTime(CertDeployEntity entity) {
        if (entity == null || entity.getCertId() == null) {
            return R.fail("数据错误");
        }

        if (entity.getIsAuto() == null || !entity.getIsAuto()) {
            return R.fail("未开启自动部署");
        }

        //有效期大于7天
        if (entity.getCertValidityDateEnd() != null &&
            entity.getCertValidityDateEnd().after(DateUtil.endOfDay(DateUtil.offsetDay(new Date(), 7)))) {
            return R.fail("证书有效期大于7天");
        }

        CertInfoEntity certInfo = certInfoService.getById(entity.getCertId());
        if (certInfo == null) {
            return R.fail("证书信息不存在");
        }

        //证书不是已完成状态
        if (!CertStatusEnum.DONE.name().equals(certInfo.getStatus())) {
            return R.fail("关联的证书状态异常");
        }

        //证书为空
        if (!StrUtil.isAllNotBlank(certInfo.getPrivateKey(), certInfo.getPublicKey())) {
            return R.fail("关联的证书秘钥为空");
        }

        //证书已过期
        if (certInfo.getValidityDateEnd() != null && certInfo.getValidityDateEnd().before(new Date())) {
            return R.fail("关联的证书已过期");
        }

        //证书私钥未更新
        if (StrUtil.isNotBlank(entity.getCertPrivateKey()) &&
            StrUtil.trim(entity.getCertPrivateKey()).equals(StrUtil.trim(certInfo.getPrivateKey()))) {
            return R.fail("证书秘钥未更新");
        }

        //获取下一个五分钟的时间
        CronTrigger cron = new CronTrigger("0 0/5 * * * ?");
        Instant instant = cron.nextExecution(new SimpleTriggerContext(new Date(), new Date(), new Date()));
        return R.data(Date.from(instant));
    }
}
