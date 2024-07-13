package com.hyx.ssl.modules.dict.param;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 字典参数
 */
@Data
@AllArgsConstructor
public class DictParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private String key;
    private String info;
}
