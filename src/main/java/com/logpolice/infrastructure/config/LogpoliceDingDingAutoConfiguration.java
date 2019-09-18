package com.logpolice.infrastructure.config;

import com.logpolice.infrastructure.properties.LogpoliceDingDingProperties;
import com.logpolice.infrastructure.rpc.DingDingNoticeRpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 钉钉配置自动装配
 *
 * @author huang
 * @date 2019/8/28
 */
@Configuration
@AutoConfigureAfter(LogpoliceAutoConfiguration.class)
@ConditionalOnProperty(name = "logpolice.notice-send-type", havingValue = "DING_DING", matchIfMissing = true)
public class LogpoliceDingDingAutoConfiguration {

    private final LogpoliceDingDingProperties logpoliceDingDingProperties;

    @Autowired
    public LogpoliceDingDingAutoConfiguration(LogpoliceDingDingProperties logpoliceDingDingProperties) {
        this.logpoliceDingDingProperties = logpoliceDingDingProperties;
    }

    @Bean
    public DingDingNoticeRpc dingDingNoticeRpc() {
        return new DingDingNoticeRpc(logpoliceDingDingProperties);
    }
}
