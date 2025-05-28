package com.hyx.ssl.modules.cert.strategy.deploy.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ssh.JschUtil;
import com.hyx.ssl.modules.auth.entity.AuthConfigEntity;
import com.hyx.ssl.modules.auth.service.IAuthConfigService;
import com.hyx.ssl.modules.cert.entity.CertDeployEntity;
import com.hyx.ssl.modules.cert.entity.CertInfoEntity;
import com.hyx.ssl.modules.cert.service.ICertInfoService;
import com.hyx.ssl.modules.cert.strategy.deploy.DeployStrategy;
import com.hyx.ssl.tool.api.R;
import com.hyx.ssl.util.DbSecureUtil;
import com.jcraft.jsch.Session;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * 七牛云cdn部署证书
 */
@Service
@AllArgsConstructor
public class ServerSshDeployStrategyImpl implements DeployStrategy {
    private final ICertInfoService certInfoService;
    private final IAuthConfigService authConfigService;

    @Override
    public R<Object> deploy(CertDeployEntity entity) throws Exception {
        if (entity == null) {
            return R.fail("deploy对象不能为空");
        }

        AuthConfigEntity authConfig = authConfigService.getById(entity.getAuthConfigId());
        if (authConfig == null) {
            return R.fail("authConfig服务配置获取失败");
        }

        if (!ObjUtil.isAllNotEmpty(authConfig.getServerSshHost(), authConfig.getServerSshPort(),
            authConfig.getServerSshUser(), authConfig.getServerSshPassword(), entity.getServerSshExec())) {
            return R.fail("服务器参数不能为空");
        }

        CertInfoEntity certInfo = certInfoService.getById(entity.getCertId());

        String serverSshPassword = DbSecureUtil.decryptFromDb(authConfig.getServerSshPassword());
        Session session = JschUtil.getSession(authConfig.getServerSshHost(), authConfig.getServerSshPort(),
            authConfig.getServerSshUser(), serverSshPassword);

        String sshExec = entity.getServerSshExec();

        //替换证书秘钥
        sshExec = sshExec
            .replace("${certPublicKey}", StrUtil.trim(certInfo.getPublicKey()))
            .replace("${certPrivateKey}", StrUtil.trim(certInfo.getPrivateKey()));

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
            String exec = JschUtil.exec(session, sshExec, StandardCharsets.UTF_8, outputStream);
            String error = outputStream.toString();
            if (StrUtil.isNotBlank(exec) || StrUtil.isBlank(error)) {
                return R.success(StrUtil.blankToDefault(exec, "执行命令成功"));
            } else {
                return R.fail("执行命令异常：" + error);
            }
        } finally {
            session.disconnect();
        }
    }
}
