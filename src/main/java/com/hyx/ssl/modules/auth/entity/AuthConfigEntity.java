package com.hyx.ssl.modules.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hyx.ssl.base.BaseEntity;
import com.hyx.ssl.modules.auth.enums.AuthConfigTypeEnum;
import lombok.Data;


/**
 * 服务配置 实体类
 */
@Data
@TableName("auth_config")
public class AuthConfigEntity extends BaseEntity {

    /**
     * 名称
     */
    @TableField(value = "name_")
    private String name;
    /**
     * 类型 {@link AuthConfigTypeEnum}
     */
    @TableField(value = "type_")
    private String type;
    /**
     * 云服务秘钥
     */
    @TableField(value = "access_key")
    private String accessKey;
    /**
     * 云服务秘钥
     */
    @TableField(value = "secret_key")
    private String secretKey;
    /**
     * 远程服务器地址
     */
    @TableField(value = "server_ssh_host")
    private String serverSshHost;
    /**
     * 远程服务器端口
     */
    @TableField(value = "server_ssh_port")
    private Integer serverSshPort;
    /**
     * 远程服务器用户
     */
    @TableField(value = "server_ssh_user")
    private String serverSshUser;
    /**
     * 远程服务器密码
     */
    @TableField(value = "server_ssh_password")
    private String serverSshPassword;
    /**
     * 飞书机器人Webhook地址
     */
    @TableField("feishu_bot_hook")
    private String feishuBotHook;
}
