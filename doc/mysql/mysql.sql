DROP TABLE IF EXISTS `cert_info`;

CREATE TABLE `cert_info` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `name_` varchar(64) DEFAULT NULL COMMENT '名称',
  `domain_` varchar(512) DEFAULT NULL COMMENT '域名',
  `domain_dns_type` varchar(32) DEFAULT NULL COMMENT '域名解析类型',
  `access_key_id` varchar(128) DEFAULT NULL COMMENT '阿里云id',
  `access_key_secret` varchar(128) DEFAULT NULL COMMENT '阿里云秘钥',
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证书信息';

