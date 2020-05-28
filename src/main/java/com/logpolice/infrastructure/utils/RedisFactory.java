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
     * @param value   值
     * @param seconds 秒
     * @return 状态
     */
    boolean setex(String key, String value, int seconds);

    /**
     * 锁
     *
     * @param key     键
     * @param value   值
     * @param seconds 秒
     * @return 状态
     */
    boolean lock(String key, String value, int seconds);

    /**
     * 解锁
     *
     * @param key 键
     * @return 状态
     */
    boolean unlock(String key);
}
