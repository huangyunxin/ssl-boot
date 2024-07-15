/*Table structure for table `auth_config` */

DROP TABLE IF EXISTS `auth_config`;

CREATE TABLE `auth_config` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `name_` varchar(64) DEFAULT NULL COMMENT '名称',
  `type_` varchar(32) DEFAULT NULL COMMENT '类型',
  `access_key` varchar(128) DEFAULT NULL COMMENT '云服务秘钥',
  `secret_key` varchar(128) DEFAULT NULL COMMENT '云服务秘钥',
  `server_ssh_host` varchar(64) DEFAULT NULL COMMENT '远程服务器地址',
  `server_ssh_port` int(11) DEFAULT NULL COMMENT '远程服务器端口',
  `server_ssh_user` varchar(128) DEFAULT NULL COMMENT '远程服务器用户',
  `server_ssh_password` varchar(128) DEFAULT NULL COMMENT '远程服务器密码',
  `feishu_bot_hook` varchar(256) DEFAULT NULL COMMENT '飞书机器人Webhook地址',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务配置表';

/*Table structure for table `cert_check` */

DROP TABLE IF EXISTS `cert_check`;

CREATE TABLE `cert_check` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `name_` varchar(64) DEFAULT NULL COMMENT '名称',
  `domain_` varchar(512) DEFAULT NULL COMMENT '域名',
  `cert_validity_date_start` date DEFAULT NULL COMMENT '有效期开始时间',
  `cert_validity_date_end` date DEFAULT NULL COMMENT '有效期结束时间',
  `msg_type` varchar(32) DEFAULT NULL COMMENT '消息提醒类型',
  `auth_config_id` bigint(20) DEFAULT NULL COMMENT '配置id',
  `last_msg_time` datetime DEFAULT NULL COMMENT '最近消息时间',
  `last_execute_time` datetime DEFAULT NULL COMMENT '最近一次执行时间',
  `log_` text COMMENT '日志',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证书监控表';

/*Table structure for table `cert_deploy` */

DROP TABLE IF EXISTS `cert_deploy`;

CREATE TABLE `cert_deploy` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `name_` varchar(64) DEFAULT NULL COMMENT '名称',
  `domain_` varchar(64) DEFAULT NULL COMMENT '域名',
  `type_` varchar(32) DEFAULT NULL COMMENT '部署类型',
  `auth_config_id` bigint(20) DEFAULT NULL COMMENT '配置id',
  `ali_oss_bucket` varchar(64) DEFAULT NULL COMMENT '阿里云OSS bucket',
  `ali_oss_endpoint` varchar(128) DEFAULT NULL COMMENT '阿里云OSS endpoint',
  `ali_cdn_endpoint` varchar(128) DEFAULT NULL COMMENT '阿里云CDN endpoint',
  `server_ssh_exec` varchar(512) DEFAULT NULL COMMENT '远程服务器SSH命令',
  `cert_id` bigint(20) DEFAULT NULL COMMENT '证书id',
  `cert_public_key` text COMMENT '证书公钥',
  `cert_private_key` text COMMENT '证书私钥',
  `cert_validity_date_start` date DEFAULT NULL COMMENT '证书有效期开始时间',
  `cert_validity_date_end` date DEFAULT NULL COMMENT '证书有效期结束时间',
  `status_` varchar(32) DEFAULT NULL COMMENT '状态',
  `log_` text COMMENT '日志',
  `is_auto` tinyint(1) DEFAULT NULL COMMENT '是否自动部署',
  `last_execute_time` datetime DEFAULT NULL COMMENT '最近一次执行时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证书部署表';

/*Table structure for table `cert_info` */

DROP TABLE IF EXISTS `cert_info`;

CREATE TABLE `cert_info` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `name_` varchar(64) DEFAULT NULL COMMENT '名称',
  `domain_` varchar(512) DEFAULT NULL COMMENT '域名',
  `domain_dns_type` varchar(32) DEFAULT NULL COMMENT '域名解析类型',
  `auth_config_id` bigint(20) DEFAULT NULL COMMENT '配置id',
  `account_private_key` text COMMENT '账户秘钥',
  `public_key` text COMMENT '公钥',
  `private_key` text COMMENT '私钥',
  `validity_date_start` date DEFAULT NULL COMMENT '有效期开始时间',
  `validity_date_end` date DEFAULT NULL COMMENT '有效期结束时间',
  `status_` varchar(32) DEFAULT NULL COMMENT '状态',
  `log_` text COMMENT '日志',
  `is_auto` tinyint(1) DEFAULT NULL COMMENT '是否自动更新',
  `is_test` tinyint(1) DEFAULT NULL COMMENT '是否测试',
  `last_execute_time` datetime DEFAULT NULL COMMENT '最近一次执行时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证书信息表';
