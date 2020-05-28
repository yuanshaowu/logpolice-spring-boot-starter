package com.logpolice.infrastructure.rpc;

import com.logpolice.infrastructure.enums.NoticeDbTypeEnum;
import com.logpolice.infrastructure.utils.LockUtils;
import com.logpolice.infrastructure.utils.RedisFactory;

import java.util.List;

/**
 * redis锁
 *
 * @author huang
 * @date 2019/9/3
 */
public class LockUtilsRedis implements LockUtils {

    private final List<RedisFactory> redisFactories;

    public LockUtilsRedis(List<RedisFactory> redisFactories) {
        this.redisFactories = redisFactories;
    }

    @Override
    public NoticeDbTypeEnum getType() {
        return NoticeDbTypeEnum.REDIS;
    }

    @Override
    public boolean lock(String key) {
        return redisFactories.get(0).lock(key, "1", 1);
    }

    @Override
    public void unlock(String key) {
        redisFactories.get(0).unlock(key);
    }

}
