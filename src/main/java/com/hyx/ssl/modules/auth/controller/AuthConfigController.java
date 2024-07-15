package com.hyx.ssl.modules.auth.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyx.ssl.modules.auth.entity.AuthConfigEntity;
import com.hyx.ssl.modules.auth.service.IAuthConfigService;
import com.hyx.ssl.modules.cert.entity.CertCheckEntity;
import com.hyx.ssl.modules.cert.entity.CertDeployEntity;
import com.hyx.ssl.modules.cert.entity.CertInfoEntity;
import com.hyx.ssl.modules.cert.service.ICertCheckService;
import com.hyx.ssl.modules.cert.service.ICertDeployService;
import com.hyx.ssl.modules.cert.service.ICertInfoService;
import com.hyx.ssl.tool.api.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/authConfig")
@AllArgsConstructor
public class AuthConfigController {
    private final IAuthConfigService authConfigService;
    private final ICertCheckService certCheckService;
    private final ICertDeployService certDeployService;
    private final ICertInfoService certInfoService;

    /**
     * 详情
     */
    @GetMapping("/{id:\\d+}")
    public R<Object> detail(@PathVariable Long id) {
        AuthConfigEntity entity = authConfigService.getById(id);
        if (entity == null) {
            return R.fail("数据不存在");
        }
        return R.data(entity);
    }

    /**
     * 列表
     */
    @GetMapping
    public R<Page<AuthConfigEntity>> list(Page<AuthConfigEntity> pageParam, String type) {
        Page<AuthConfigEntity> page = authConfigService.page(pageParam, Wrappers.lambdaQuery(AuthConfigEntity.class)
            .eq(StrUtil.isNotBlank(type), AuthConfigEntity::getType, type)
            .orderByDesc(AuthConfigEntity::getId));
        return R.data(page);
    }

    /**
     * 新增或修改
     */
    @PostMapping
    public R<Page<AuthConfigEntity>> submit(@RequestBody AuthConfigEntity entity) {
        authConfigService.saveOrUpdate(entity);
        return R.status(true);
    }

    /**
     * 删除
     */
    @DeleteMapping
    public R<Page<AuthConfigEntity>> deleted(@RequestBody Map<String, Object> map) {
        List<String> idsStr = (List<String>) map.get("ids");
        if (CollUtil.isEmpty(idsStr)) {
            return R.fail("参数不能为空");
        }

        List<Long> ids = idsStr.stream().map(Long::valueOf).toList();
        //已使用的不允许删除
        for (Long id : ids) {
            if (certCheckService.count(Wrappers.lambdaQuery(CertCheckEntity.class)
                .eq(CertCheckEntity::getAuthConfigId, id)) > 0 ||
                certDeployService.count(Wrappers.lambdaQuery(CertDeployEntity.class)
                    .eq(CertDeployEntity::getAuthConfigId, id)) > 0 ||
                certInfoService.count(Wrappers.lambdaQuery(CertInfoEntity.class)
                    .eq(CertInfoEntity::getAuthConfigId, id)) > 0) {
                return R.fail("该配置已使用，不允许删除");
            }
        }

        authConfigService.removeByIds(ids);
        return R.status(true);
    }
}
