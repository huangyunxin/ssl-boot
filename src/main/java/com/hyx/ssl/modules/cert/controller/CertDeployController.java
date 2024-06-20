package com.hyx.ssl.modules.cert.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyx.ssl.modules.cert.entity.CertDeployEntity;
import com.hyx.ssl.modules.cert.service.ICertDeployService;
import com.hyx.ssl.tool.api.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/certDeploy")
@AllArgsConstructor
public class CertDeployController {
    private final ICertDeployService certDeployService;

    /**
     * 详情
     */
    @GetMapping("/{id:\\d+}")
    public R<Object> detail(@PathVariable Long id) {
        CertDeployEntity entity = certDeployService.getById(id);
        if (entity == null) {
            return R.fail("数据不存在");
        }
        return R.data(entity);
    }

    /**
     * 列表
     */
    @GetMapping
    public R<Page<CertDeployEntity>> list(Page<CertDeployEntity> pageParam) {
        Page<CertDeployEntity> page = certDeployService.page(pageParam, Wrappers.lambdaQuery(CertDeployEntity.class)
            .orderByDesc(CertDeployEntity::getId));
        //添加是否自动执行
        page.getRecords().forEach(item -> {
            R<Date> r = certDeployService.getNextExecuteTime(item);
            item.setNextExecuteTime(r.isSuccess() ? DateUtil.formatDateTime(r.getData()) : r.getMsg());
        });
        return R.data(page);
    }

    /**
     * 新增或修改
     */
    @PostMapping
    public R<Page<CertDeployEntity>> submit(@RequestBody CertDeployEntity entity) {
        //只读项不修改
        entity.setCertPublicKey(null);
        entity.setCertPrivateKey(null);
        entity.setCertValidityDateStart(null);
        entity.setCertValidityDateEnd(null);
        entity.setStatus(null);
        entity.setLog(null);
        entity.setLastExecuteTime(null);

        //修改了证书，则清空数据
        if (entity.getId() != null) {
            CertDeployEntity oldEntity = certDeployService.getById(entity.getId());
            if (oldEntity != null && !oldEntity.getCertId().equals(entity.getCertId())) {
                certDeployService.update(Wrappers.lambdaUpdate(CertDeployEntity.class)
                    .set(CertDeployEntity::getStatus, null)
                    .set(CertDeployEntity::getLog, null)
                    .set(CertDeployEntity::getLastExecuteTime, null)
                    .set(CertDeployEntity::getCertPublicKey, null)
                    .set(CertDeployEntity::getCertPrivateKey, null)
                    .set(CertDeployEntity::getCertValidityDateStart, null)
                    .set(CertDeployEntity::getCertValidityDateEnd, null)
                    .eq(CertDeployEntity::getId, entity.getId()));
            }
        }

        certDeployService.saveOrUpdate(entity);
        return R.status(true);
    }

    /**
     * 删除
     */
    @DeleteMapping
    public R<Page<CertDeployEntity>> deleted(@RequestBody Map<String, Object> map) {
        List<Long> ids = (List<Long>) map.get("ids");
        if (CollUtil.isEmpty(ids)) {
            return R.fail("参数不能为空");
        }

        certDeployService.removeByIds(ids);
        return R.status(true);
    }

    /**
     * 部署证书
     */
    @PostMapping("/nowDeploy")
    public R<Page<CertDeployEntity>> nowUpdate(@RequestBody CertDeployEntity entity) {
        if (entity.getId() == null) {
            return R.fail("参数不能为空");
        }

        CertDeployEntity certDeploy = certDeployService.getById(entity.getId());
        if (certDeploy == null) {
            return R.fail("数据不存在");
        }

        //部署证书
        certDeployService.deployCert(certDeploy);
        return R.fail("执行中，请稍后查看日志");
    }
}
