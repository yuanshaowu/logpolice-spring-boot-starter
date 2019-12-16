package com.logpolice.infrastructure.utils;

import com.logpolice.infrastructure.enums.NoticeDbTypeEnum;

/**
 * 锁工具类
 *
 * @author huang
 * @date 2019/9/3
 */
public interface LockUtils {

    /**
     * 获取DB类型
     *
     * @return 仓储类型
     */
    NoticeDbTypeEnum getType();

    /**
     * 加锁
     *
     * @param key 键
     * @return 是否成功
     */
    boolean lock(String key);

    /**
     * 解锁
     *
     * @param key 键
     */
    void unlock(String key);
}
