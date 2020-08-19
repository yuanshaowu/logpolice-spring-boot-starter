package com.logpolice.infrastructure.config;

import com.logpolice.infrastructure.utils.ApplicationContextProvider;
import com.logpolice.infrastructure.utils.LogpoliceExecutorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志配置自动装配
 *
 * @author huang
 * @date 2019/8/28
 */
@Configuration
@Import(ApplicationContextProvider.class)
public class LogpoliceAutoConfiguration {

    @Bean
    public LogpoliceExecutorFactory logpoliceExecutorFactory() {
        return new LogpoliceExecutorFactory(new ConcurrentHashMap<>());
    }

}
