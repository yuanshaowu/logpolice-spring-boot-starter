package com.logpolice.infrastructure.config;

import com.logpolice.infrastructure.rpc.ExceptionStatisticLocalCache;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地缓存自动装配
 *
 * @author huang
 * @date 2019/8/28
 */
@Configuration
@AutoConfigureAfter({LogpoliceMailAutoConfiguration.class, LogpoliceDingDingAutoConfiguration.class})
public class LogpoliceLoclCacheAutoConfiguration {

    @Bean
    public ExceptionStatisticLocalCache exceptionStatisticLocalCache() {
        return new ExceptionStatisticLocalCache(new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
    }
}
