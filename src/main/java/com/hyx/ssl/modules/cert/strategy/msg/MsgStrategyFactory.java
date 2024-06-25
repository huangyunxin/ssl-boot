package com.hyx.ssl.modules.cert.strategy.msg;

import com.hyx.ssl.modules.cert.enums.MsgTypeEnum;
import com.hyx.ssl.modules.cert.strategy.msg.impl.FeishuBotHookStrategyImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MsgStrategyFactory {
    private final FeishuBotHookStrategyImpl feishuBotHookStrategy;

    public MsgStrategy getCardStrategy(String type) {
        if (MsgTypeEnum.FEISHU_BOT_HOOK.name().equals(type)) {
            return feishuBotHookStrategy;
        }

        throw new RuntimeException("msg类型不可用");
    }
}
