package com.hyx.ssl.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

/**
 * 数据库敏感数据加密
 */
public class DbSecureUtil {
    private static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCagMWDSJFiI9A//X3ebCVsEfIHB6thmF0jos9RuKxXFQCV3KEnpPm558/HarVojuuKPf0c8un97jtE1LzjHgy/GBXFglTsDEHk1ZFSRqnD2ksnd4x0fnPkFDCQfUeJqdcXGv2sGATYEozjp4W+sgnbOT4/65jxhEeuPOBYqAj8eQIDAQAB";
    private static final String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJqAxYNIkWIj0D/9fd5sJWwR8gcHq2GYXSOiz1G4rFcVAJXcoSek+bnnz8dqtWiO64o9/Rzy6f3uO0TUvOMeDL8YFcWCVOwMQeTVkVJGqcPaSyd3jHR+c+QUMJB9R4mp1xca/awYBNgSjOOnhb6yCds5Pj/rmPGER6484FioCPx5AgMBAAECgYBKLzVnDXN9D1whNYp3vy6SvuUamgSgpkNmZnisMQNTBssSqe6ZNf4QVO1vv11eWG1hIdWETmCq4/YCXTFazeVpJl3gA5k7UGjpLLAajqoO2QyY9zPxQLQZzChIPMPtSjk660deMZQq5U8vyiw+qgr1/n/14L1KHlyY0nXduUBT9wJBANXml/o01nP8mj1AjNQz7GbEEr8iioVD95bJyXpFhSOus5e61Xwah9+S16AlmJ7xeQr3GjipikBwptkymwih3ScCQQC46Wr/sWtlwZt/Fuofj1RjtG1q/oC/zSseMlJCs9YT9Q2m4jG+/O90EkDEfYPDaYqv6Es3u00vkaQHCA3aUZ1fAkEApEBnUZ3DU9uUQRbRTZ31mVBVKOqIPh9b/zFCgp7hxu2/QPMaPitNTPRAmdxk3yCEF1R6kSo3XJZkuQJwJGfPHwJAGaLS4Mw6NYtYAZCtuN5oNsKHAPRz6SOKvM8BNJo2LeIlmGN3viDXGeKF9DfqkqcJQUYVV46yLswkT41ATmpORQJASzKg8UENUuynTeE4j7O7/QM1/cXwAf+l4nw+fsKEsyzQOQkz9rP873x+du0QbVXBAK9iCptYxKJsljJnNS4aXw==";

    /**
     * 明文加密到数据库
     */
    public static String encryptToDb(String content) {
        if (StrUtil.isBlank(content)) {
            return null;
        }

        return SecureUtil.rsa(privateKey, publicKey).encryptBase64(content, KeyType.PublicKey);
    }

    /**
     * 数据库解密到明文
     */
    public static String decryptFromDb(String content) {
        if (StrUtil.isBlank(content)) {
            return null;
        }

        return SecureUtil.rsa(privateKey, publicKey).decryptStr(content, KeyType.PrivateKey);
    }

    public static void main(String[] args) {
        RSA rsa = SecureUtil.rsa();
        String publicKeyStr = rsa.getPublicKeyBase64();
        String privateKeyStr = rsa.getPrivateKeyBase64();
        System.out.println("公钥：" + publicKeyStr);
        System.out.println("私钥：" + privateKeyStr);
        String plainText = "hello world";
        System.out.println("加密前：" + plainText);
        String cipherText = SecureUtil.rsa(privateKeyStr, publicKeyStr).encryptBase64(plainText, KeyType.PublicKey);
        System.out.println("加密后：" + cipherText);
        String decryptText = SecureUtil.rsa(privateKeyStr, publicKeyStr).decryptStr(cipherText, KeyType.PrivateKey);
        System.out.println("解密后：" + decryptText);
    }
}
