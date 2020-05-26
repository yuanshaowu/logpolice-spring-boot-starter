package com.logpolice.infrastructure.rpc;

import com.logpolice.infrastructure.enums.NoticeDbTypeEnum;
import com.logpolice.infrastructure.utils.LockUtils;
import com.logpolice.infrastructure.utils.RedisUtils;

/**
 * redis锁
 *
 * @author huang
 * @date 2019/9/3
 */
public class LockUtilsRedis implements LockUtils {

    private final RedisUtils redisUtils;

    public LockUtilsRedis(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    @Override
    public NoticeDbTypeEnum getType() {
        return NoticeDbTypeEnum.REDIS;
    }

    @Override
    public boolean lock(String key) {
        return redisUtils.lock(key, "1", 1);
    }

    @Override
    public void unlock(String key) {
        redisUtils.del(key);
    }
}
