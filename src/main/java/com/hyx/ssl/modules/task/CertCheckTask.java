package com.hyx.ssl.modules.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.hyx.ssl.modules.cert.entity.CertCheckEntity;
import com.hyx.ssl.modules.cert.service.ICertCheckService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CertCheckTask {
    private final ICertCheckService certCheckService;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void certCheckUpdate() {
        System.out.println("证书监控：开始");
        //查询所有证书
        List<CertCheckEntity> list = certCheckService.list();

        System.out.println(StrUtil.format("证书监控：待更新数量：{}", CollUtil.size(list)));
        for (CertCheckEntity entity : list) {
            //更新证书
            System.out.println(StrUtil.format("证书监控：更新证书：{}", entity.getName()));
            certCheckService.updateAndSendMsg(entity);
        }

        System.out.println("证书监控：结束");
    }
}
