package com.logpolice.infrastructure.properties;

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
    String getWebHook();

    /**
     * 被@人的手机号
     */
    Set<String> getAtMobiles();

    /**
     * 此消息类型为固定text
     */
    String getMsgType();

    /**
     * 所有人@时：true，否则为false
     */
    Boolean getIsAtAll();
}
