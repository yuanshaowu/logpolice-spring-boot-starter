package com.logpolice.infrastructure.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * 日志报警钉钉配置
 *
 * @author huang
 * @date 2019/8/29
 */
@ConfigurationProperties(prefix = "logpolice.dingding")
public class LogpoliceDingDingProperties {

    /**
     * webhook
     */
    @Getter
    @Setter
    private String webHook;

    /**
     * 被@人的手机号
     */
    @Getter
    @Setter
    private Set<String> atMobiles = new HashSet<>();

    /**
     * 此消息类型为固定text
     */
    @Getter
    @Setter
    private String msgType = LogpoliceConstant.DING_DING_TEXT;

    /**
     * 所有人@时：true，否则为false
     */
    @Getter
    @Setter
    private Boolean isAtAll = LogpoliceConstant.DING_DING_IS_AT_ALL;
}
