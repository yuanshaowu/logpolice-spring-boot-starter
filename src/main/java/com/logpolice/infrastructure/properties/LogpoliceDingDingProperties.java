package com.logpolice.infrastructure.properties;

import java.util.HashSet;
import java.util.Set;

/**
 * 日志报警钉钉配置
 *
 * @author huang
 * @date 2019/8/29
 */
public interface LogpoliceDingDingProperties {

    /**
     * webhook
     */
    String getDingDingWebHook();

    /**
     * 被@人的手机号
     */
    default Set<String> getDingDingAtMobiles() {
        return new HashSet<>();
    }

    /**
     * 此消息类型为固定text
     */
    default String getDingDingMsgType() {
        return LogpoliceConstant.DING_DING_TEXT;
    }

    /**
     * 所有人@时：true，否则为false
     */
    default Boolean getDingDingIsAtAll() {
        return LogpoliceConstant.DING_DING_IS_AT_ALL;
    }
}
