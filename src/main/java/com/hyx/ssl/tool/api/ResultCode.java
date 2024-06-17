//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hyx.ssl.tool.api;

import lombok.Getter;

@Getter
public enum ResultCode implements IResultCode {
    SUCCESS(200, "操作成功"),
    FAILURE(400, "业务异常");

    final int code;
    final String message;

    ResultCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }
}
