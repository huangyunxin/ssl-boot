package com.hyx.ssl.modules.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyx.ssl.modules.auth.entity.AuthConfigEntity;
import com.hyx.ssl.tool.api.R;

public interface IAuthConfigService extends IService<AuthConfigEntity> {

    /**
     * 新增或修改
     */
    R<AuthConfigEntity> submit(AuthConfigEntity entity);
}
