package com.hyx.ssl.modules.cert.strategy.deploy.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ssh.JschUtil;
import com.hyx.ssl.modules.cert.entity.CertDeployEntity;
import com.hyx.ssl.modules.cert.entity.CertInfoEntity;
import com.hyx.ssl.modules.cert.service.ICertInfoService;
import com.hyx.ssl.modules.cert.strategy.deploy.DeployStrategy;
import com.hyx.ssl.tool.api.R;
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

    @Override
    public R<Object> deploy(CertDeployEntity entity) throws Exception {
        if (entity == null) {
            return R.fail("deploy对象不能为空");
        }

        if (!ObjUtil.isAllNotEmpty(entity.getServerSshHost(), entity.getServerSshPort(),
            entity.getServerSshUser(), entity.getServerSshPassword(), entity.getServerSshExec())) {
            return R.fail("服务器参数不能为空");
        }

        CertInfoEntity certInfo = certInfoService.getById(entity.getCertId());

        Session session = JschUtil.getSession(entity.getServerSshHost(), entity.getServerSshPort(),
            entity.getServerSshUser(), entity.getServerSshPassword());

        String sshExec = entity.getServerSshExec();

        //替换证书秘钥
        sshExec = sshExec
            .replace("${certPublicKey}", StrUtil.trim(certInfo.getPublicKey()))
            .replace("${certPrivateKey}", StrUtil.trim(certInfo.getPrivateKey()));

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
            String exec = JschUtil.exec(session, sshExec, StandardCharsets.UTF_8, outputStream);
            String error = outputStream.toString();
            if (StrUtil.isNotBlank(exec) || StrUtil.isBlank(error)) {
                return R.status(true);
            } else {
                return R.fail("执行命令异常：" + error);
            }
        } finally {
            session.disconnect();
        }
    }
}
