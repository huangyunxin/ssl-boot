package com.hyx.ssl.modules.cert.strategy.msg.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hyx.ssl.modules.cert.entity.CertCheckEntity;
import com.hyx.ssl.modules.cert.strategy.msg.MsgStrategy;
import com.hyx.ssl.tool.api.R;
import org.springframework.stereotype.Service;

/**
 * 飞书机器人消息
 */
@Service
public class FeishuBotHookStrategyImpl implements MsgStrategy {

    @Override
    public R<Object> sendMsg(CertCheckEntity entity, String msg) {
        if (entity == null || StrUtil.isBlank(entity.getFeishuBotHook()) || StrUtil.isBlank(msg)) {
            return R.fail("消息参数不能为空");
        }

        String webhookUrl = entity.getFeishuBotHook();
        String body = "{" +
            "   \"msg_type\": \"text\"," +
            "   \"content\": {\"text\":\"" + msg + "\"}}";

        String res = HttpUtil.post(webhookUrl, body);
        JSONObject resObj = JSONUtil.parseObj(res);
        Integer code = resObj.getInt("code");
        if (code == 0) {
            return R.success(res);
        } else {
            return R.fail(res);
        }
    }
}
