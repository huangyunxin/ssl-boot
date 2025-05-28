package com.hyx.ssl.modules.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyx.ssl.modules.auth.entity.AuthConfigEntity;
import com.hyx.ssl.modules.auth.mapper.AuthConfigMapper;
import com.hyx.ssl.modules.auth.service.IAuthConfigService;
import com.hyx.ssl.tool.api.R;
import com.hyx.ssl.util.DbSecureUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthConfigServiceImpl extends ServiceImpl<AuthConfigMapper, AuthConfigEntity> implements IAuthConfigService {
    @Override
    public R<AuthConfigEntity> submit(AuthConfigEntity entity) {
        //敏感数据加密存储
        entity.setAccessKey(StrUtil.isNotBlank(entity.getAccessKey()) ?
            DbSecureUtil.encryptToDb(entity.getAccessKey()) : null);
        entity.setSecretKey(StrUtil.isNotBlank(entity.getSecretKey()) ?
            DbSecureUtil.encryptToDb(entity.getSecretKey()) : null);
        entity.setServerSshPassword(StrUtil.isNotBlank(entity.getServerSshPassword()) ?
            DbSecureUtil.encryptToDb(entity.getServerSshPassword()) : null);

        this.saveOrUpdate(entity);
        return R.data(entity);
    }
}
