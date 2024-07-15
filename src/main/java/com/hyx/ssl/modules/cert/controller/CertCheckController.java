package com.hyx.ssl.modules.cert.controller;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyx.ssl.modules.cert.entity.CertCheckEntity;
import com.hyx.ssl.modules.cert.service.ICertCheckService;
import com.hyx.ssl.tool.api.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/certCheck")
@AllArgsConstructor
public class CertCheckController {
    private final ICertCheckService certCheckService;

    /**
     * 详情
     */
    @GetMapping("/{id:\\d+}")
    public R<Object> detail(@PathVariable Long id) {
        CertCheckEntity entity = certCheckService.getById(id);
        if (entity == null) {
            return R.fail("数据不存在");
        }
        return R.data(entity);
    }

    /**
     * 列表
     */
    @GetMapping
    public R<Page<CertCheckEntity>> list(Page<CertCheckEntity> pageParam) {
        Page<CertCheckEntity> page = certCheckService.page(pageParam, Wrappers.lambdaQuery(CertCheckEntity.class)
            .orderByAsc(CertCheckEntity::getCertValidityDateEnd, CertCheckEntity::getId));
        return R.data(page);
    }

    /**
     * 新增或修改
     */
    @PostMapping
    public R<Page<CertCheckEntity>> submit(@RequestBody CertCheckEntity entity) {
        //只读项不修改
        entity.setCertValidityDateStart(null);
        entity.setCertValidityDateEnd(null);
        entity.setLastExecuteTime(null);
        entity.setLastMsgTime(null);
        entity.setLog(null);

        certCheckService.saveOrUpdate(entity);
        return R.status(true);
    }

    /**
     * 删除
     */
    @DeleteMapping
    public R<Page<CertCheckEntity>> deleted(@RequestBody Map<String, Object> map) {
        List<Long> ids = (List<Long>) map.get("ids");
        if (CollUtil.isEmpty(ids)) {
            return R.fail("参数不能为空");
        }

        certCheckService.removeByIds(ids);
        return R.status(true);
    }

    /**
     * 更新并发送消息
     */
    @PostMapping("/updateAndSendMsg")
    public R<Page<CertCheckEntity>> updateAndSendMsg(@RequestBody CertCheckEntity entity) {
        if (entity.getId() == null) {
            return R.fail("参数不能为空");
        }

        CertCheckEntity certCheck = certCheckService.getById(entity.getId());
        if (certCheck == null) {
            return R.fail("数据不存在");
        }

        //部署证书
        certCheckService.updateAndSendMsg(certCheck);
        return R.success("执行完毕，请查看日志");
    }
}
