package com.hyx.ssl.modules.dict.controller;


import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.hyx.ssl.modules.auth.enums.AuthConfigTypeEnum;
import com.hyx.ssl.modules.cert.enums.*;
import com.hyx.ssl.modules.dict.param.DictParam;
import com.hyx.ssl.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典控制器
 */
@RestController
@RequestMapping("/dict")
public class DictController {

    private static Map<String, Class<?>> enumClassMap = new HashMap<>();

    static {
        enumClassMap.put("authConfigTypeEnum", AuthConfigTypeEnum.class);
        enumClassMap.put("certStatusEnum", CertStatusEnum.class);
        enumClassMap.put("deployTypeEnum", DeployTypeEnum.class);
        enumClassMap.put("domainDnsTypeEnum", DomainDnsTypeEnum.class);
        enumClassMap.put("msgTypeEnum", MsgTypeEnum.class);
    }

    /**
     * 枚举字典
     */
    @GetMapping("/enums/{name}")
    public R<List<DictParam>> enums(@PathVariable String name) {
        Class<? extends Enum> enumClass = (Class<? extends Enum>) enumClassMap.get(name);
        if (enumClass == null) {
            return R.fail("枚举不存在");
        }

        List<DictParam> dictList = new ArrayList<>();
        Enum[] enumConstants = enumClass.getEnumConstants();
        for (Enum enumConstant : enumConstants) {
            String enumInfo = (String) ReflectUtil.getFieldValue(enumConstant, "info");
            String enumKey = enumConstant.name();
            if (StrUtil.isAllNotBlank(enumInfo, enumKey)) {
                dictList.add(new DictParam(enumKey, enumInfo));
            }
        }
        return R.data(dictList);
    }
}
