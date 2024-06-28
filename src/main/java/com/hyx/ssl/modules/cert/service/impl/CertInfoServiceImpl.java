package com.hyx.ssl.modules.cert.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyx.ssl.modules.cert.entity.CertInfoEntity;
import com.hyx.ssl.modules.cert.enums.CertStatusEnum;
import com.hyx.ssl.modules.cert.enums.DomainRecordTypeEnum;
import com.hyx.ssl.modules.cert.mapper.CertInfoMapper;
import com.hyx.ssl.modules.cert.param.DomainRecord;
import com.hyx.ssl.modules.cert.service.ICertInfoService;
import com.hyx.ssl.modules.cert.strategy.dns.DnsStrategyFactory;
import com.hyx.ssl.tool.api.R;
import com.hyx.ssl.util.DomainUtil;
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
import java.util.*;
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
                throw new Exception("更新失败：数据不存在");
            }

            //重置数据
            this.update(Wrappers.lambdaUpdate(CertInfoEntity.class)
                .set(CertInfoEntity::getStatus, CertStatusEnum.UNDERWAY.name())
                .set(CertInfoEntity::getLastExecuteTime, new Date())
                .set(CertInfoEntity::getPublicKey, null)
                .set(CertInfoEntity::getPrivateKey, null)
                .set(CertInfoEntity::getValidityDateStart, null)
                .set(CertInfoEntity::getLog, null)
                .eq(CertInfoEntity::getId, entity.getId()));

            //重新查询entity
            entity = this.getById(entity.getId());

            //创建ACME服务器session
            log.append("\n==========创建ACME服务器session开始==========");
            Session session = null;
            if (entity.getIsTest() != null && entity.getIsTest()) {
                log.append("\n创建测试环境session");
                session = new Session("acme://letsencrypt.org/staging");
            } else {
                log.append("\n创建正式环境session");
                session = new Session("acme://letsencrypt.org");
            }
            log.append("\n==========创建ACME服务器session结束==========");

            //读取密钥
            log.append("\n==========读取密钥开始==========");
            KeyPair keyPair = null;
            String accountPrivateKey = entity.getAccountPrivateKey();
            if (StrUtil.isBlank(accountPrivateKey)) {
                log.append("\n秘钥不存在，创建密钥");
                //不存在则创建密钥
                try (StringWriter stringWriter = new StringWriter()) {
                    keyPair = KeyPairUtils.createKeyPair(2048);
                    KeyPairUtils.writeKeyPair(keyPair, stringWriter);
                    entity.setAccountPrivateKey(stringWriter.toString());
                } catch (Exception e) {
                    log.append(StrUtil.format("\n创建密钥异常：{}", e.getMessage()));
                    throw e;
                }
            } else {
                log.append("\n秘钥已存在，读取秘钥");
                try (StringReader stringReader = new StringReader(accountPrivateKey)) {
                    keyPair = KeyPairUtils.readKeyPair(stringReader);
                } catch (Exception e) {
                    log.append(StrUtil.format("\n读取密钥异常：{}", e.getMessage()));
                    throw e;
                }
            }
            log.append("\n==========读取密钥结束==========");


            //创建账户
            log.append("\n==========创建账户开始==========");
            Account account = null;
            try {
                account = new AccountBuilder()
                    .addContact("mailto:hyx@qq.com")
                    .agreeToTermsOfService()
                    .useKeyPair(keyPair)
                    .create(session);
                log.append("\n创建账户成功");
            } catch (Exception e) {
                log.append(StrUtil.format("\n创建账户异常：{}", e.getMessage()));
                throw e;
            }
            log.append("\n==========创建账户结束==========");


            //创建订单
            log.append("\n==========创建订单开始==========");
            Order order = null;
            try {
                Set<String> domains = Arrays.stream(entity.getDomain().split(","))
                    .map(item -> StrUtil.trim(item))
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toSet());
                if (CollUtil.isEmpty(domains)) {
                    throw new Exception("创建订单异常：域名不能为空");
                }
                log.append(StrUtil.format("\n创建订单：域名：{}", JSONUtil.toJsonStr(domains)));
                order = account.newOrder()
                    .domains(domains)
                    .create();
                log.append("\n创建订单成功");
            } catch (Exception e) {
                log.append(StrUtil.format("\n创建订单异常：{}", e.getMessage()));
                throw e;
            }
            log.append("\n==========创建订单结束==========");

            try {
                List<DomainRecord> recordList = new ArrayList<>();
                log.append("\n==========域名验证开始==========");
                for (Authorization auth : order.getAuthorizations()) {
                    Optional<Dns01Challenge> challengeOpt = auth.findChallenge(Dns01Challenge.TYPE);
                    Dns01Challenge challenge = challengeOpt.get();
                    String domain = auth.getIdentifier().getDomain();
                    String resourceName = Dns01Challenge.toRRName(auth.getIdentifier());
                    String digest = challenge.getDigest();

                    //获取二级域名
                    String secondLevelDomain = DomainUtil.getSecondLevelDomain(domain);
                    //获取主机记录前缀
                    String domainPrefix = resourceName.replace("." + secondLevelDomain + ".", "");
                    log.append(StrUtil.format("\n添加DNS解析：二级域名：{}，主机记录前缀：{}，记录值：{}", secondLevelDomain, domainPrefix, digest));
                    //添加DNS解析
                    R<Object> dnsR = dnsStrategyFactory.getCardStrategy(entity.getDomainDnsType())
                        .addDomainRecord(entity, secondLevelDomain, domainPrefix, DomainRecordTypeEnum.TXT, digest);
                    if (!dnsR.isSuccess()) {
                        log.append(StrUtil.format("\n添加DNS解析异常：{}", dnsR.getMsg()));
                        throw new Exception(dnsR.getMsg());
                    }
                    recordList.add(new DomainRecord(secondLevelDomain, domainPrefix));

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
                //删除解析记录
                if (CollUtil.isNotEmpty(recordList)) {
                    for (DomainRecord record : recordList) {
                        try {
                            log.append(StrUtil.format("\n删除DNS记录：{}", record.getDomainPrefix()));
                            R<Object> dnsR = dnsStrategyFactory.getCardStrategy(entity.getDomainDnsType())
                                .deleteDomainRecord(entity, record.getDomain(), record.getDomainPrefix());
                            if (dnsR.isSuccess()) {
                                log.append(StrUtil.format("\n删除DNS记录成功：{}", record.getDomainPrefix()));
                            } else {
                                log.append(StrUtil.format("\n删除DNS记录失败：{}", record.getDomainPrefix()));
                            }
                        } catch (Exception e) {
                            log.append(StrUtil.format("\n删除DNS记录异常：{}", e.getMessage()));
                        }
                    }
                }
                log.append("\n==========域名验证结束==========");
            } catch (Exception e) {
                log.append(StrUtil.format("\nDNS验证异常：{}", e.getMessage()));
                throw e;
            }

            //完成订单
            log.append("\n==========完成订单开始==========");
            KeyPair domainKeyPair = null;
            try {
                domainKeyPair = KeyPairUtils.createKeyPair(2048);
                order.execute(domainKeyPair);
            } catch (Exception e) {
                log.append(StrUtil.format("\n完成订单异常：{}", e.getMessage()));
                throw e;
            }
            log.append("\n==========完成订单结束==========");


            //判断证书状态
            log.append("\n==========判断证书状态开始==========");
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
            log.append("\n==========判断证书状态结束==========");

            //获取证书
            log.append("\n==========获取证书开始==========");
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
            log.append("\n==========获取证书完成==========");
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
