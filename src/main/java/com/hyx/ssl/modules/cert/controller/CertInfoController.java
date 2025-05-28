package com.hyx.ssl.modules.cert.controller;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyx.ssl.modules.cert.entity.CertInfoEntity;
import com.hyx.ssl.modules.cert.enums.CertStatusEnum;
import com.hyx.ssl.modules.cert.service.ICertInfoService;
import com.hyx.ssl.tool.api.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/certInfo")
@AllArgsConstructor
public class CertInfoController {
    private final ICertInfoService certInfoService;

    /**
     * 详情
     */
    @GetMapping("/{id:\\d+}")
    public R<Object> detail(@PathVariable Long id) {
        CertInfoEntity entity = certInfoService.getById(id);
        if (entity == null) {
            return R.fail("数据不存在");
        }
        return R.data(entity);
    }

    /**
     * 列表
     */
    @GetMapping
    public R<Page<CertInfoEntity>> list(Page<CertInfoEntity> page) {
        return R.data(certInfoService.page(page, Wrappers.lambdaQuery(CertInfoEntity.class)
            .orderByAsc(CertInfoEntity::getValidityDateEnd, CertInfoEntity::getId)));
    }

    /**
     * 新增或修改
     */
    @PostMapping
    public R<Page<CertInfoEntity>> submit(@RequestBody CertInfoEntity entity) {
        //只读项不修改
        entity.setAccountPrivateKey(null);
        entity.setPublicKey(null);
        entity.setPrivateKey(null);
        entity.setValidityDateStart(null);
        entity.setValidityDateEnd(null);
        entity.setStatus(null);
        entity.setLog(null);
        entity.setLastExecuteTime(null);
        certInfoService.saveOrUpdate(entity);
        return R.status(true);
    }

    /**
     * 删除
     */
    @DeleteMapping
    public R<Page<CertInfoEntity>> deleted(@RequestBody Map<String, Object> map) {
        List<Long> ids = (List<Long>) map.get("ids");
        if (CollUtil.isEmpty(ids)) {
            return R.fail("参数不能为空");
        }

        certInfoService.removeByIds(ids);
        return R.status(true);
    }

    /**
     * 更新证书
     */
    @PostMapping("/nowUpdate")
    public R<Page<CertInfoEntity>> nowUpdate(@RequestBody CertInfoEntity entity) {
        if (entity.getId() == null) {
            return R.fail("参数不能为空");
        }

        CertInfoEntity certInfo = certInfoService.getById(entity.getId());
        if (certInfo == null) {
            return R.fail("数据不存在");
        }

        if (CertStatusEnum.UNDERWAY.name().equals(certInfo.getStatus())) {
            return R.fail("更新失败，证书当前状态申请中，请稍后再试");
        }

        //异步更新
        certInfoService.updateCertAsync(certInfo);
        return R.status(true);
    }
}
