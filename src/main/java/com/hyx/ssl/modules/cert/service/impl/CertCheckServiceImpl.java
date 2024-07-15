package com.hyx.ssl.modules.cert.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyx.ssl.modules.cert.entity.CertCheckEntity;
import com.hyx.ssl.modules.cert.mapper.CertCheckMapper;
import com.hyx.ssl.modules.cert.service.ICertCheckService;
import com.hyx.ssl.modules.cert.strategy.msg.MsgStrategyFactory;
import com.hyx.ssl.tool.api.R;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

@Service
@AllArgsConstructor
public class CertCheckServiceImpl extends ServiceImpl<CertCheckMapper, CertCheckEntity> implements ICertCheckService {
    private final MsgStrategyFactory msgStrategyFactory;

    @Override
    public void updateAndSendMsg(CertCheckEntity entity) {
        StringBuffer log = new StringBuffer(StrUtil.format("==========开始 {}==========", DateUtil.formatDateTime(new Date())));

        //重置数据
        this.update(Wrappers.lambdaUpdate(CertCheckEntity.class)
            .set(CertCheckEntity::getCertValidityDateStart, null)
            .set(CertCheckEntity::getCertValidityDateEnd, null)
            .eq(CertCheckEntity::getId, entity.getId()));

        //重新查询
        entity = this.getById(entity.getId());

        try {
            // 打开URL连接
            String url = "https://" + StrUtil.trim(entity.getDomain());
            log.append(StrUtil.format("\n打开URL连接：{}", url));
            HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
            // 连接
            conn.connect();
            // 获取SSL会话
            SSLSession session = conn.getSSLSession().get();
            // 获取证书
            Certificate[] certs = session.getPeerCertificates();
            if (ArrayUtil.isEmpty(certs)) {
                throw new Exception("证书为空");
            }
            for (Certificate cert : certs) {
                if (cert instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate) cert;
                    // 获取证书有效期的起始时间
                    entity.setCertValidityDateStart(x509Cert.getNotBefore());
                    // 获取证书有效期的结束时间
                    entity.setCertValidityDateEnd(x509Cert.getNotAfter());
                    //只获取第一个证书
                    break;
                }
            }

            // 关闭连接
            conn.disconnect();

            if (entity.getCertValidityDateStart() == null || entity.getCertValidityDateEnd() == null) {
                throw new Exception("\n证书有效期获取失败");
            }

            //判断8-17点，则发送提醒
            int hour = DateUtil.hour(new Date(), true);
            if (8 <= hour && hour <= 17) {
                //判断证书有效期是否小于等于7天
                if (DateUtil.betweenDay(new Date(), entity.getCertValidityDateEnd(), true) <= 7) {
                    //判断今天是否提醒过
                    if (entity.getLastMsgTime() == null || !DateUtil.isSameDay(entity.getLastMsgTime(), new Date())) {
                        R<Object> sendMsgR = msgStrategyFactory.getCardStrategy(entity.getMsgType()).sendMsg(entity,
                            StrUtil.format("{} 证书即将过期，到期时间 {}",
                                StrUtil.trim(entity.getDomain()),
                                DateUtil.formatDateTime(entity.getCertValidityDateEnd())));
                        log.append(StrUtil.format("\n发送消息{}：{}", sendMsgR.isSuccess() ? "成功" : "失败", sendMsgR.getMsg()));
                        entity.setLastMsgTime(new Date());
                    }
                }
            }

            log.append(StrUtil.format("\n==========完成 {}==========", DateUtil.formatDateTime(new Date())));
        } catch (Exception e) {
            try (StringWriter stringWriter = new StringWriter();
                 PrintWriter printWriter = new PrintWriter(stringWriter)) {
                e.printStackTrace(printWriter);
                log.append(StrUtil.format("\n执行失败：{}", stringWriter.toString()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            entity.setLastExecuteTime(new Date());
            entity.setLog(log.toString());
            this.updateById(entity);
        }
    }
}
