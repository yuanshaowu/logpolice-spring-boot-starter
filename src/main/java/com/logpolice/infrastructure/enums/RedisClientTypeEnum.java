package com.logpolice.infrastructure.enums;

/**
 * redis客户端类型
 *
 * @author huang
 * @date 2019/8/28
 */
public enum RedisClientTypeEnum {

    /**
     * LOCAL_CACHE：本地
     * REDIS：redis
     */
    REDIS_TEMPLATE,
    JEDIS;

}
