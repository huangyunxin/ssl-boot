package com.hyx.ssl.modules.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyx.ssl.modules.auth.entity.AuthConfigEntity;
import com.hyx.ssl.modules.auth.mapper.AuthConfigMapper;
import com.hyx.ssl.modules.auth.service.IAuthConfigService;
import org.springframework.stereotype.Service;

@Service
public class AuthConfigServiceImpl extends ServiceImpl<AuthConfigMapper, AuthConfigEntity> implements IAuthConfigService {
}
