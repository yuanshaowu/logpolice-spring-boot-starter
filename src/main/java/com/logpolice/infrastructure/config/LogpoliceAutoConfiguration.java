package com.logpolice.infrastructure.config;

import com.logpolice.infrastructure.properties.LogpoliceProperties;
import com.logpolice.infrastructure.utils.ApplicationContextProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 日志配置自动装配
 *
 * @author huang
 * @date 2019/8/28
 */
@Configuration
@EnableConfigurationProperties(LogpoliceProperties.class)
@Import(ApplicationContextProvider.class)
public class LogpoliceAutoConfiguration {
}
