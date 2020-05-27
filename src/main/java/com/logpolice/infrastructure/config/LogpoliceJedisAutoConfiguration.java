package com.logpolice.infrastructure.config;

import com.logpolice.infrastructure.utils.redis.JedisUtils;
import com.logpolice.infrastructure.utils.redis.RedisTemplateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisSentinelPool;

/**
 * redis配置自动装配
 *
 * @author huang
 * @date 2019/8/28
 */
@Configuration
@AutoConfigureAfter({LogpoliceMailAutoConfiguration.class, LogpoliceDingDingAutoConfiguration.class, LogpoliceRedisTemplateAutoConfiguration.class})
@ConditionalOnClass(JedisSentinelPool.class)
@ConditionalOnMissingBean(RedisTemplateUtils.class)
public class LogpoliceJedisAutoConfiguration {

    private final JedisSentinelPool jedisSentinelPool;

    @Autowired
    public LogpoliceJedisAutoConfiguration(JedisSentinelPool jedisSentinelPool) {
        this.jedisSentinelPool = jedisSentinelPool;
    }

    @Bean
    public JedisUtils jedisUtils() {
        return new JedisUtils(jedisSentinelPool);
    }

}
