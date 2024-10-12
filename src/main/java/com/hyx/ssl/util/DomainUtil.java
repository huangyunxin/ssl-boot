package com.hyx.ssl.util;

import cn.hutool.core.util.StrUtil;

/**
 * 域名工具类
 */
public class DomainUtil {

    /**
     * 获取域名前缀（www.example.com返回www）
     */
    public static String getDomainPrefix(String domain) {
        if (StrUtil.isBlank(domain)) {
            return null;
        }

        //获取二级域名
        String secondLevelDomain = getSecondLevelDomain(domain);
        if (StrUtil.isBlank(secondLevelDomain)) {
            return null;
        }

        return domain.replace("." + secondLevelDomain, "");
    }

    /**
     * 获取二级域名（www.example.com返回example.com）
     */
    public static String getSecondLevelDomain(String domain) {
        if (StrUtil.isBlank(domain)) {
            return null;
        }

        //去掉协议头
        domain = StrUtil.trim(domain.replace("http://", "").replace("https://", ""));

        String[] split = domain.split("\\.");
        if (split.length < 2) {
            return null;
        }

        //特殊判断.org.cn的域名
        if (domain.endsWith(".org.cn")) {
            return split[split.length - 3] + "." + split[split.length - 2] + "." + split[split.length - 1];
        }

        return split[split.length - 2] + "." + split[split.length - 1];
    }
}
