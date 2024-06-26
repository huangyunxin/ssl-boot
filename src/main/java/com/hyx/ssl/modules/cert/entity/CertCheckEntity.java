package com.hyx.ssl.modules.cert.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 证书监控 实体类
 */
@Data
@TableName("cert_check")
public class CertCheckEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableField("id")
    private Long id;
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
     * 消息提醒类型 {@link com.hyx.ssl.modules.cert.enums.MsgTypeEnum}
     */
    @TableField("msg_type")
    private String msgType;
    /**
     * 飞书机器人Webhook地址
     */
    @TableField("feishu_bot_hook")
    private String feishuBotHook;
    /**
     * 最近消息时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("last_msg_time")
    private java.util.Date lastMsgTime;
    /**
     * 最近一次执行时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("last_execute_time")
    private java.util.Date lastExecuteTime;
    /**
     * 日志
     */
    @TableField("log_")
    private String log;
}
