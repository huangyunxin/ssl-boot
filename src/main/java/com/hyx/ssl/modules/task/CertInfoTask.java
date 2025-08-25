package com.hyx.ssl.modules.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hyx.ssl.modules.cert.entity.CertInfoEntity;
import com.hyx.ssl.modules.cert.enums.CertStatusEnum;
import com.hyx.ssl.modules.cert.service.ICertInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class CertInfoTask {
    private final ICertInfoService certInfoService;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void certInfoUpdate() {
         log.info("证书自动更新：开始");
        //查询需要更新的证书
        List<CertInfoEntity> list = certInfoService.list(Wrappers.lambdaQuery(CertInfoEntity.class)
            .in(CertInfoEntity::getStatus, CertStatusEnum.DONE, CertStatusEnum.FAIL)
            .in(CertInfoEntity::getIsAuto, true)
            .isNotNull(CertInfoEntity::getValidityDateEnd)
            //查询10天内到期的
            .le(CertInfoEntity::getValidityDateEnd, DateUtil.formatDate(DateUtil.offsetDay(new Date(), 10)))
        );

         log.info(StrUtil.format("证书自动更新：待更新数量：{}", CollUtil.size(list)));
        for (CertInfoEntity entity : list) {
            //更新证书
             log.info(StrUtil.format("证书自动更新：更新证书：{}", entity.getName()));
            certInfoService.updateCertAsync(entity);
        }

         log.info("证书自动更新：结束");
    }
}
