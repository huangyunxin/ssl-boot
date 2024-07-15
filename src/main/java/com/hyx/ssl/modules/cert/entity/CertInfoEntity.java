package com.hyx.ssl.modules.cert.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hyx.ssl.base.BaseEntity;
import com.hyx.ssl.modules.cert.enums.DomainDnsTypeEnum;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * 证书信息 实体类
 */
@Data
@TableName("cert_info")
public class CertInfoEntity extends BaseEntity {

    /**
     * 名称
     */
    @TableField("name_")
    private String name;
    /**
     * 域名
     */
    @TableField("domain_")
    private String domain;
    /**
     * 域名解析类型 {@link DomainDnsTypeEnum}
     */
    @TableField("domain_dns_type")
    private String domainDnsType;
    /**
     * 配置id
     */
    @TableField("auth_config_id")
    private Long authConfigId;
    /**
     * 账户秘钥
     */
    @TableField("account_private_key")
    private String accountPrivateKey;
    /**
     * 公钥
     */
    @TableField("public_key")
    private String publicKey;
    /**
     * 私钥
     */
    @TableField("private_key")
    private String privateKey;
    /**
     * 有效期开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("validity_date_start")
    private java.util.Date validityDateStart;
    /**
     * 有效期结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("validity_date_end")
    private java.util.Date validityDateEnd;
    /**
     * 状态 {@link com.hyx.ssl.modules.cert.enums.CertStatusEnum}
     */
    @TableField("status_")
    private String status;
    /**
     * 日志
     */
    @TableField("log_")
    private String log;
    /**
     * 是否自动更新
     */
    @TableField("is_auto")
    private Boolean isAuto;
    /**
     * 是否测试
     */
    @TableField("is_test")
    private Boolean isTest;
    /**
     * 最近一次执行时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("last_execute_time")
    private java.util.Date lastExecuteTime;
}
