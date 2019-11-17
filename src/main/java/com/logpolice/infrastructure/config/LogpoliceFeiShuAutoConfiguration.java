package com.logpolice.infrastructure.config;

import com.logpolice.infrastructure.properties.LogpoliceFeiShuProperties;
import com.logpolice.infrastructure.rpc.DingDingNoticeRpc;
import com.logpolice.infrastructure.rpc.FeiShuNoticeRpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 飞书配置自动装配
 *
 * @author huang
 * @date 2019/8/28
 */
@Configuration
@AutoConfigureAfter(LogpoliceAutoConfiguration.class)
public class LogpoliceFeiShuAutoConfiguration {

    private final LogpoliceFeiShuProperties logpoliceFeiShuProperties;

    @Autowired
    public LogpoliceFeiShuAutoConfiguration(LogpoliceFeiShuProperties logpoliceFeiShuProperties) {
        this.logpoliceFeiShuProperties = logpoliceFeiShuProperties;
    }

    @Bean
    public FeiShuNoticeRpc feiShuNoticeRpc() {
        return new FeiShuNoticeRpc(logpoliceFeiShuProperties);
    }
}
