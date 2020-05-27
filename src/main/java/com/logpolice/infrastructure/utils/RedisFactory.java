package com.logpolice.infrastructure.utils;

import com.logpolice.infrastructure.enums.RedisClientTypeEnum;

/**
 * redis工厂
 *
 * @author huang
 * @date 2020/5/27
 */
public interface RedisFactory {

    /**
     * 获取类型
     *
     * @return 类型
     */
    RedisClientTypeEnum getType();

    /**
     * 获取值
     *
     * @param key 键
     * @return 值
     */
    String get(String key);

    /**
     * 设置值
     *
     * @param key     键
     * @param seconds 秒
     * @param value   值
     * @return 状态
     */
    String setex(String key, int seconds, String value);

    /**
     * 锁
     *
     * @param lockKey    键
     * @param value      值
     * @param lockSecond 秒
     * @return
     */
    boolean lock(String lockKey, String value, int lockSecond);

    /**
     * 删除
     *
     * @param key 键
     * @return 状态
     */
    Long del(String key);
}
