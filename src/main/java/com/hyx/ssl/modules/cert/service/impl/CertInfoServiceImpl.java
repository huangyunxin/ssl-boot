package com.hyx.ssl.modules.cert.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyx.ssl.modules.cert.entity.CertInfoEntity;
import com.hyx.ssl.modules.cert.enums.CertStatusEnum;
import com.hyx.ssl.modules.cert.mapper.CertInfoMapper;
import com.hyx.ssl.modules.cert.service.ICertInfoService;
import com.hyx.ssl.modules.cert.strategy.DnsStrategyFactory;
import com.hyx.ssl.tool.api.R;
import lombok.AllArgsConstructor;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.shredzone.acme4j.*;
import org.shredzone.acme4j.challenge.Dns01Challenge;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CertInfoServiceImpl extends ServiceImpl<CertInfoMapper, CertInfoEntity> implements ICertInfoService {
    private final DnsStrategyFactory dnsStrategyFactory;

    @Override
    public void updateCert(CertInfoEntity entity) {
        StringBuffer log = new StringBuffer(StrUtil.format("==========更新开始 {}==========", DateUtil.formatDateTime(new Date())));
        try {
            if (entity == null) {
                throw new RuntimeException("更新失败：数据不存在");
            }

            //清空旧数据
            this.update(Wrappers.lambdaUpdate(CertInfoEntity.class)
                .set(CertInfoEntity::getStatus, CertStatusEnum.UNDERWAY.name())
                .set(CertInfoEntity::getPublicKey, null)
                .set(CertInfoEntity::getPrivateKey, null)
                .set(CertInfoEntity::getValidityDateStart, null)
                .set(CertInfoEntity::getValidityDateEnd, null)
                .set(CertInfoEntity::getLog, null)
                .eq(CertInfoEntity::getId, entity.getId()));

            //创建ACME服务器session
            Session session = null;
            if (entity.getIsTest() != null && entity.getIsTest()) {
                //测试url
                session = new Session("acme://letsencrypt.org/staging");
            } else {
                session = new Session("acme://letsencrypt.org");
            }

            //读取密钥对
            log.append("\n==========读取密钥对==========");
            KeyPair keyPair = null;
            String accountPrivateKey = entity.getAccountPrivateKey();
            if (StrUtil.isBlank(accountPrivateKey)) {
                //不存在则创建密钥
                try (StringWriter stringWriter = new StringWriter()) {
                    keyPair = KeyPairUtils.createKeyPair(2048);
                    KeyPairUtils.writeKeyPair(keyPair, stringWriter);
                    entity.setAccountPrivateKey(stringWriter.toString());
                } catch (Exception e) {
                    log.append(StrUtil.format("\n创建密钥对异常：{}", e.getMessage()));
                    throw e;
                }
            } else {
                try (StringReader stringReader = new StringReader(accountPrivateKey)) {
                    keyPair = KeyPairUtils.readKeyPair(stringReader);
                } catch (Exception e) {
                    log.append(StrUtil.format("\n读取密钥对异常：{}", e.getMessage()));
                    throw e;
                }
            }


            //创建账户
            Account account = null;
            try {
                log.append("\n==========创建账户==========");
                account = new AccountBuilder()
                    .addContact("mailto:hyx@qq.com")
                    .agreeToTermsOfService()
                    .useKeyPair(keyPair)
                    .create(session);
            } catch (Exception e) {
                log.append(StrUtil.format("\n创建账户异常：{}", e.getMessage()));
                throw e;
            }


            //创建订单
            Order order = null;
            try {
                log.append("\n==========创建订单==========");
                Set<String> domains = Arrays.stream(entity.getDomain().split(","))
                    .map(item -> StrUtil.trim(item))
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toSet());
                if (CollUtil.isEmpty(domains)) {
                    throw new RuntimeException("域名不能为空");
                }

                order = account.newOrder()
                    .domains(domains)
                    .create();
            } catch (Exception e) {
                log.append(StrUtil.format("\n创建订单异常：{}", e.getMessage()));
                throw e;
            }

            try {
                log.append("\n==========域名验证==========");
                for (Authorization auth : order.getAuthorizations()) {
                    Optional<Dns01Challenge> challengeOpt = auth.findChallenge(Dns01Challenge.TYPE);
                    Dns01Challenge challenge = challengeOpt.get();
                    String domain = auth.getIdentifier().getDomain();
                    String resourceName = Dns01Challenge.toRRName(auth.getIdentifier());
                    String digest = challenge.getDigest();

                    //设置DNS
                    String[] split = domain.split("\\.");
                    String domainRoot = ArrayUtil.get(split, split.length - 2) + "." + ArrayUtil.get(split, split.length - 1);
                    String rrKeyWord = resourceName.replace("." + domainRoot + ".", "");
                    log.append(StrUtil.format("\ndomainRoot：{},rrKeyWord：{},digest：{}", domainRoot, rrKeyWord, digest));
                    log.append("\n设置DNS");
                    R<Object> dnsR = dnsStrategyFactory.getCardStrategy(entity.getDomainDnsType())
                        .addDomainRecord(entity, domainRoot, rrKeyWord, "TXT", digest);
                    if (!dnsR.isSuccess()) {
                        log.append(StrUtil.format("\n设置DNS异常：{}", dnsR.getMsg()));
                        throw new RuntimeException(dnsR.getMsg());
                    }

                    //触发DNS验证
                    log.append("\n触发DNS验证");
                    challenge.trigger();

                    //获取验证状态
                    log.append("\n获取验证状态");
                    int i = 0;
                    while (true) {
                        Thread.sleep(3000L);
                        log.append(StrUtil.format("\nDNS状态：{}", auth.getStatus()));
                        if (Status.VALID == auth.getStatus()) {
                            break;
                        }
                        auth.fetch();

                        if (i >= 99) {
                            log.append("\nDNS验证超时");
                            return;
                        }
                        i++;
                    }
                }
            } catch (Exception e) {
                log.append(StrUtil.format("\nDNS验证异常：{}", e.getMessage()));
                throw e;
            }

            //完成订单
            log.append("\n==========完成订单==========");
            KeyPair domainKeyPair = null;
            try {
                domainKeyPair = KeyPairUtils.createKeyPair(2048);
                order.execute(domainKeyPair);
            } catch (Exception e) {
                log.append(StrUtil.format("\n完成订单异常：{}", e.getMessage()));
                throw e;
            }


            //判断证书状态
            log.append("\n==========判断证书状态==========");
            try {
                int i = 0;
                while (true) {
                    Thread.sleep(3000L);
                    log.append(StrUtil.format("\n证书状态：{}", order.getStatus()));
                    if (Status.VALID == order.getStatus()) {
                        break;
                    }
                    order.fetch();

                    if (i >= 99) {
                        log.append("\n证书状态验证超时");
                        return;
                    }
                    i++;
                }
            } catch (Exception e) {
                log.append(StrUtil.format("\n判断证书状态异常：{}", e.getMessage()));
                throw e;
            }

            //获取证书
            log.append("\n==========获取证书==========");
            Certificate cert = order.getCertificate();
            try (StringWriter publicKeyWriter = new StringWriter();
                 StringWriter privateKeyWriter = new StringWriter();
                 JcaPEMWriter pr = new JcaPEMWriter(privateKeyWriter)) {
                //公钥
                cert.writeCertificate(publicKeyWriter);
                entity.setPublicKey(publicKeyWriter.toString());
                //私钥
                pr.writeObject(domainKeyPair.getPrivate());
                pr.flush();
                entity.setPrivateKey(privateKeyWriter.toString());
                //证书有效期
                X509Certificate certificate = cert.getCertificate();
                entity.setValidityDateStart(certificate.getNotBefore());
                entity.setValidityDateEnd(certificate.getNotAfter());
            } catch (Exception e) {
                log.append(StrUtil.format("\n获取证书异常：{}", e.getMessage()));
                throw e;
            }
            log.append(StrUtil.format("\n==========更新完成 {}==========", DateUtil.formatDateTime(new Date())));
        } catch (Exception e) {
            try (StringWriter stringWriter = new StringWriter();
                 PrintWriter printWriter = new PrintWriter(stringWriter)) {
                e.printStackTrace(printWriter);
                log.append(StrUtil.format("\n更新失败：{}", stringWriter.toString()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            entity.setStatus(StrUtil.isNotBlank(entity.getPrivateKey()) ? CertStatusEnum.DONE.name() : CertStatusEnum.FAIL.name());
            entity.setLog(log.toString());
            this.updateById(entity);
        }
    }

    @Async
    @Override
    public void updateCertAsync(CertInfoEntity entity) {
        this.updateCert(entity);
    }
}
