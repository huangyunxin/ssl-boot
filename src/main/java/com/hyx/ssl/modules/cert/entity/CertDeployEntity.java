package com.hyx.ssl.modules.cert.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * 证书部署 实体类
 */
@Data
@TableName("cert_deploy")
public class CertDeployEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableField(value = "id")
    private Long id;
    /**
     * 名称
     */
    @TableField(value = "name_")
    private String name;
    /**
     * 域名
     */
    @TableField(value = "domain_")
    private String domain;
    /**
     * 部署类型 {@link com.hyx.ssl.modules.cert.enums.DeployTypeEnum}
     */
    @TableField(value = "type_")
    private String type;
    /**
     * 阿里云Access Key ID
     */
    @TableField(value = "ali_access_key_id")
    private String aliAccessKeyId;
    /**
     * 阿里云Access Key Secret
     */
    @TableField(value = "ali_access_key_secret")
    private String aliAccessKeySecret;
    /**
     * 阿里云OSS Bucket名称
     */
    @TableField(value = "ali_oss_bucket")
    private String aliOssBucket;
    /**
     * 阿里云OSS Endpoint
     */
    @TableField(value = "ali_oss_endpoint")
    private String aliOssEndpoint;
    /**
     * 证书id
     */
    @TableField(value = "cert_id")
    private Long certId;
    /**
     * 证书公钥
     */
    @TableField(value = "cert_public_key")
    private String certPublicKey;
    /**
     * 证书私钥
     */
    @TableField(value = "cert_private_key")
    private String certPrivateKey;
    /**
     * 证书有效期开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("cert_validity_date_start")
    private java.util.Date certValidityDateStart;
    /**
     * 证书有效期结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("cert_validity_date_end")
    private java.util.Date certValidityDateEnd;
    /**
     * 状态 {@link com.hyx.ssl.modules.cert.enums.CertStatusEnum}
     */
    @TableField(value = "status_")
    private String status;
    /**
     * 日志
     */
    @TableField(value = "log_")
    private String log;
    /**
     * 是否自动部署
     */
    @TableField(value = "is_auto")
    private Boolean isAuto;
    /**
     * 最近一次执行时间
     */
    @TableField(value = "last_execute_time")
    private Date lastExecuteTime;
}
