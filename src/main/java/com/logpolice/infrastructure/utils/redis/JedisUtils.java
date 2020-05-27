package com.logpolice.infrastructure.utils.redis;

import com.logpolice.infrastructure.enums.RedisClientTypeEnum;
import com.logpolice.infrastructure.utils.RedisFactory;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Arrays;
import java.util.Collections;

/**
 * jedis工具类
 *
 * @author huang
 * @date 2019/8/29
 */
@Slf4j
public class JedisUtils implements RedisFactory {

    private static final String PRE = "logpolice_spring_boot_starter_";
    private static final String PRE_LOCK = "logpolice_spring_boot_starter_lock_";

    private static final Long RELEASE_SUCCESS = 1L;

    private final JedisSentinelPool shardedJedisPool;

    public JedisUtils(JedisSentinelPool shardedJedisPool) {
        this.shardedJedisPool = shardedJedisPool;
    }

    @Override
    public RedisClientTypeEnum getType() {
        return RedisClientTypeEnum.JEDIS;
    }

    @Override
    public Long del(String key) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.del(PRE + key);
        } catch (Exception e) {
            log.warn("JedisUtils class del Method is Exception", e);
        }
        return ret;
    }

    @Override
    public String get(String key) {
        String ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.get(PRE + key);
        } catch (Exception e) {
            log.warn("JedisUtils class get Method is Exception", e);
        }
        return ret;
    }

    @Override
    public String setex(String key, int seconds, String value) {
        String ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.setex(PRE + key, seconds * 1000, value);
        } catch (Exception e) {
            log.warn("JedisUtils class setex Method is Exception", e);
        }
        return ret;
    }

    @Override
    public boolean lock(String lockKey, String value, int lockSecond) {
        String script = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then redis.call('pexpire', KEYS[1], " +
                "ARGV[2]) return 1 else return 0 end";
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            Object obj = shardedJedis.eval(script,
                    Collections.singletonList(PRE_LOCK + lockKey),
                    Arrays.asList(PRE_LOCK + value, String.valueOf(lockSecond * 1000)));
            if (RELEASE_SUCCESS.equals(obj)) {
                return true;
            }
        } catch (Exception e) {
            log.warn("JedisUtils class setex Method is Exception", e);
        }
        return false;
    }

}
