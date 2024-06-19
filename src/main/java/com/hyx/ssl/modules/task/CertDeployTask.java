package com.hyx.ssl.modules.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.hyx.ssl.modules.cert.entity.CertDeployEntity;
import com.hyx.ssl.modules.cert.service.ICertDeployService;
import com.hyx.ssl.tool.api.R;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@AllArgsConstructor
public class CertDeployTask {
    private final ICertDeployService certDeployService;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void reportCurrentTime() {
        System.out.println("证书自动部署：开始");

        //筛选需要更新的证书
        List<CertDeployEntity> list = certDeployService.list().stream()
            .filter(item -> {
                R<Date> autoExecute = certDeployService.getNextExecuteTime(item);
                return autoExecute.isSuccess();
            }).toList();

        System.out.println(StrUtil.format("证书自动部署：待部署数量：{}", CollUtil.size(list)));
        for (CertDeployEntity entity : list) {
            //更新证书
            System.out.println(StrUtil.format("证书自动部署：部署证书：{}", entity.getName()));
            try {
                certDeployService.deployCert(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("证书自动部署：结束");
    }
}
