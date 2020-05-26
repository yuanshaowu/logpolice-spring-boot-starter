package com.logpolice.infrastructure.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.*;

/**
 * redis工具类
 *
 * @author huang
 * @date 2019/8/29
 */
public class RedisUtils {

    private static final String PRE = "logpolice_spring_boot_starter_";
    private static final String PRE_LOCK = "logpolice_spring_boot_starter_lock_";
    private static final Logger logger = LoggerFactory.getLogger("RedisUtils");

    private static final Long RELEASE_SUCCESS = 1L;

    private final JedisSentinelPool shardedJedisPool;

    public RedisUtils(JedisSentinelPool shardedJedisPool) {
        this.shardedJedisPool = shardedJedisPool;
    }

    /**
     * set缓存数据
     *
     * @param key
     * @param value
     * @return
     */
    public String set(String key, String value) {
        String ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.set(PRE + key, value);
        } catch (Exception e) {
            logger.warn("RedisUtils class set Method is Exception", e);
        }
        return ret;
    }

    /**
     * 删除缓存
     *
     * @param key
     * @return
     */
    public Long del(String key) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.del(PRE + key);
        } catch (Exception e) {
            logger.warn("RedisUtils class del Method is Exception", e);
        }
        return ret;
    }

    /**
     * 批量清理缓存
     *
     * @param keys
     * @return
     */
    public Long batchDel(String keys) {
        Long ret = null;
        ret = 0L;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            if (shardedJedis != null) {
                ret = delGroup(shardedJedis, keys);
            }
        } catch (Exception e) {
            logger.warn("RedisUtils class batchDel Method is Exception", e);
        }
        return ret;
    }

    private Long delGroup(Jedis jedis, String keys) {
        Long ret = 0L;
        Set<String> keySet = jedis.keys(keys);
        if (keySet != null && keySet.size() > 0) {
            String[] keyArr = new String[keySet.size()];
            ret = jedis.del(keySet.toArray(keyArr));
        }
        return ret;
    }

    /**
     * 清理全部缓存
     *
     * @return
     */
    public String delAll() {
        String ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            if (shardedJedis != null) {
                ret = shardedJedis.flushDB();
            }
        } catch (Exception e) {
            logger.warn("RedisUtils class delAll Method is Exception", e);
        }
        return ret;
    }

    /**
     * 判断是否存在
     *
     * @param key
     * @return
     */
    public Boolean exists(String key) {
        Boolean ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.exists(PRE + key);
        } catch (Exception e) {
            logger.warn("RedisUtils class exists Method is Exception", e);
        }
        return ret;
    }

    /**
     * 多少秒后失效
     *
     * @param key
     * @param seconds
     * @return
     */
    public Long expire(String key, int seconds) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.expire(PRE + key, seconds);
            return ret;
        } catch (Exception e) {
            logger.warn("RedisUtils class expire Method is Exception", e);
        }
        return ret;
    }

    /**
     * 时间点失效
     *
     * @param key
     * @param unixTime
     * @return
     */
    public Long expireAt(String key, long unixTime) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.expireAt(PRE + key, unixTime);
        } catch (Exception e) {
            logger.warn("RedisUtils class expireAt Method is Exception", e);
        }
        return ret;
    }

    /**
     * 获取value
     *
     * @param key
     * @return
     */
    public String get(String key) {
        String ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.get(PRE + key);
        } catch (Exception e) {
            logger.warn("RedisUtils class get Method is Exception", e);
        }
        return ret;
    }

    /**
     * 设置失效时间
     *
     * @param key
     * @param seconds
     * @param value
     * @return
     */
    public String setex(String key, int seconds, String value) {
        String ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.setex(PRE + key, seconds, value);
        } catch (Exception e) {
            logger.warn("RedisUtils class setex Method is Exception", e);
        }
        return ret;
    }

    public boolean lock(String lockKey, String value, int lockSecond) {
        String script = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then redis.call('pexpire', KEYS[1], " +
                "ARGV[2]) return 1 else return 0 end";
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            Object obj = shardedJedis.eval(script, Collections.singletonList(PRE_LOCK + lockKey), Arrays.asList(PRE_LOCK + value, String
                    .valueOf(lockSecond * 1000)));
            if (RELEASE_SUCCESS.equals(obj)) {
                return true;
            }
        } catch (Exception e) {
            logger.warn("RedisUtils class setex Method is Exception", e);
        }
        return false;
    }

    public boolean unLock(String lockKey, String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else " +
                "return 0 end";
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            Object obj = shardedJedis.eval(script, Collections.singletonList(PRE_LOCK + lockKey), Collections.singletonList
                    (PRE_LOCK + value));
            if (RELEASE_SUCCESS.equals(obj)) {
                return true;
            }
        } catch (Exception e) {
            logger.warn("RedisUtils class setex Method is Exception", e);
        }
        return false;
    }

    public boolean eval(String script) {
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            Object obj = shardedJedis.eval(script);
            if (RELEASE_SUCCESS.equals(obj)) {
                return true;
            }
        } catch (Exception e) {
            logger.warn("RedisUtils class eval Method is Exception", e);
        }
        return false;
    }

    public Long zadd(String key, double score, String member) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.zadd(PRE + key, score, member);
        } catch (Exception e) {
            logger.warn("RedisUtils class zadd Method is Exception", e);
        }
        return ret;
    }

    /**
     * zset 倒序输出
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<String> zrevrange(String key, long start, long end) {
        Set<String> ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.zrevrange(PRE + key, start, end);
        } catch (Exception e) {
            logger.warn("RedisUtils class zrevrange Method is Exception", e);
        }
        return ret;
    }

    public Long zrem(String key, String member) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.zrem(PRE + key, member);
        } catch (Exception e) {
            logger.warn("RedisUtils class zrem Method is Exception", e);
        }
        return ret;
    }

    /**
     * 将存储在键处的数字增加一个
     *
     * @param key
     * @return Long
     */
    public Long incr(String key) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.incr(PRE + key);
        } catch (Exception e) {
            logger.warn("RedisUtils class incr Method is Exception", e);
        }
        return ret;
    }

    /**
     * 将键的整数值按给定的数值增加
     *
     * @param key
     * @param integer
     * @return Long
     */
    public Long incrBy(String key, long integer) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.incrBy(PRE + key, integer);
        } catch (Exception e) {
            logger.warn("RedisUtils class incrBy Method is Exception", e);
        }
        return ret;
    }

    /**
     * 将键的浮点值按给定的数值增加
     *
     * @param key
     * @param integer
     * @return Double
     */
    public Double incrByFloat(String key, double integer) {
        Double ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.incrByFloat(PRE + key, integer);
        } catch (Exception e) {
            logger.warn("RedisUtils class incrByFloat Method is Exception", e);
        }
        return ret;
    }

    /**
     * 键的整数值减少1
     *
     * @param key
     * @return Long
     */
    public Long decr(String key) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.decr(PRE + key);
        } catch (Exception e) {
            logger.warn("RedisUtils class decr Method is Exception", e);
        }
        return ret;
    }

    /**
     * 按给定数值减少键的整数值
     *
     * @param key
     * @param integer
     * @return Long
     */
    public Long decrBy(String key, long integer) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.decrBy(PRE + key, integer);
        } catch (Exception e) {
            logger.warn("RedisUtils class decrBy Method is Exception", e);
        }
        return ret;
    }

    /**
     * 存储哈希数据
     *
     * @param key
     * @param field
     * @param vlaue
     * @return Long
     */
    public Long hSet(String key, String field, String vlaue) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.hset(PRE + key, field, vlaue);
        } catch (Exception e) {
            logger.warn("RedisUtils class hSet Method is Exception", e);
        }
        return ret;
    }

    /**
     * 获取hash数据
     *
     * @param key
     * @param field
     * @return String
     */
    public String hGet(String key, String field) {
        String ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.hget(PRE + key, field);
        } catch (Exception e) {
            logger.warn("RedisUtils class hGet Method is Exception", e);
        }
        return ret;
    }

    /**
     * 存储set数据
     *
     * @param key
     * @param field
     * @return Long
     */
    public Long sAdd(String key, String[] field) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.sadd(PRE + key, field);
        } catch (Exception e) {
            logger.warn("RedisUtils class sAdd Method is Exception", e);
        }
        return ret;
    }

    /**
     * 获取set所有数据集
     *
     * @param key
     * @return Set<String>
     */
    public Set<String> sMembers(String key) {
        Set<String> ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.smembers(PRE + key);
        } catch (Exception e) {
            logger.warn("RedisUtils class sMembers Method is Exception", e);
        }
        return ret;
    }

    /**
     * list右侧插入数据
     *
     * @param key
     * @param field
     * @return Long
     */
    public Long rPush(String key, String[] field) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.rpush(PRE + key, field);
        } catch (Exception e) {
            logger.warn("RedisUtils class rPush Method is Exception", e);
        }
        return ret;
    }

    /**
     * 获取list长度
     *
     * @param key
     * @return Long
     */
    public Long lLen(String key) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.llen(PRE + key);
        } catch (Exception e) {
            logger.warn("RedisUtils class lLen Method is Exception", e);
        }
        return ret;
    }

    /**
     * 获取list指定偏移量数据集
     *
     * @param key
     * @param start
     * @param end
     * @return List<String>
     */
    public List<String> lRange(String key, Long start, Long end) {
        List<String> ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.lrange(PRE + key, start, end);
        } catch (Exception e) {
            logger.warn("RedisUtils class lRange Method is Exception", e);
        }
        return ret;
    }

    /**
     * 获取集合的成员数
     *
     * @param key
     * @return
     */
    public Long sCard(String key) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.scard(PRE + key);
        } catch (Exception e) {
            logger.warn("RedisUtils class sCard Method is Exception", e);
        }
        return ret;
    }

    /**
     * 只有在字段 field 不存在时，设置哈希表字段的值。
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public long hSetnx(String key, String field, String value) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.hsetnx(PRE + key, field, value);
        } catch (Exception e) {
            logger.warn("RedisUtils class hSetnx Method is Exception", e);
        }
        return ret;
    }

    /**
     * 以秒为单位，返回给定 key 的剩余生存时
     *
     * @param key
     * @return
     */
    public long ttl(String key) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.ttl(PRE + key);
        } catch (Exception e) {
            logger.warn("RedisUtils class ttl Method is Exception", e);
        }
        return ret;
    }

    /**
     * 向 sorted set 中添加值
     *
     * @param key
     * @param members
     * @return
     */
    public Long zAdd(String key, Map<String, Double> members) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.zadd(PRE + key, members);
        } catch (Exception e) {
            logger.warn("RedisUtils class zAdd Method is Exception", e);
        }
        return ret;
    }

    /**
     * 有序集合中对指定成员的分数加上增量
     *
     * @param key
     * @param score
     * @param member
     * @return
     */
    public Double zIncryBy(String key, Double score, String member) {
        Double ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.zincrby(PRE + key, score, member);
        } catch (Exception e) {
            logger.warn("RedisUtils class zIncryBy Method is Exception", e);
        }
        return ret;
    }

    public double hIncryBy(String key, String value, double member) {
        Double ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.hincrByFloat(PRE + key, value, member);
        } catch (Exception e) {
            logger.warn("RedisUtils class hIncryBy Method is Exception", e);
        }
        return ret;
    }

    public Set<String> zrangeByScore(String key, double min, double max) {
        Set<String> ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.zrangeByScore(PRE + key, min, max);
        } catch (Exception e) {
            logger.warn("RedisUtils class zrangeByScore Method is Exception", e);
        }
        return ret;
    }

    public Set<String> zrangeByScore(String key, String min, String max) {
        Set<String> ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.zrangeByScore(PRE + key, min, max);
        } catch (Exception e) {
            logger.warn("RedisUtils class zrangeByScore Method is Exception", e);
        }
        return ret;
    }


    public Long zLexcount(String key, String min, String max) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.zlexcount(PRE + key, min, max);
        } catch (Exception e) {
            logger.warn("RedisUtils class zLexcount Method is Exception", e);
        }
        return ret;
    }

    /**
     * Zcount 命令用于计算有序集合中指定分数区间的成员数量。
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zCount(String key, String min, String max) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.zcount(PRE + key, min, max);
        } catch (Exception e) {
            logger.warn("RedisUtils class zCount Method is Exception", e);
        }
        return ret;
    }

    /**
     * Zcount 命令用于计算有序集合中指定分数区间的成员数量。
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zCount(String key, double min, double max) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.zcount(PRE + key, min, max);
        } catch (Exception e) {
            logger.warn("RedisUtils class zCount Method is Exception", e);
        }
        return ret;
    }

    /**
     * Zcount 命令用于计算有序集合中指定分数区间的成员数量。
     *
     * @param key
     * @param fields
     * @return
     */
    public Long hDel(String key, String[] fields) {
        Long ret = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            ret = shardedJedis.hdel(PRE + key, fields);
        } catch (Exception e) {
            logger.warn("RedisUtils class hDel Method is Exception", e);
        }
        return ret;
    }

    /**
     * mget获取缓存值
     *
     * @param keys
     * @return
     */
    public List<String> mget(List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return null;
        }
        List<String> mkeys = new ArrayList<>();
        for (String k : keys) {
            mkeys.add(PRE + k);
        }
        List<String> mval = null;
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            mval = shardedJedis.mget(mkeys.toArray(new String[mkeys.size()]));
        } catch (Exception e) {
            logger.warn("RedisUtils class mget Method is Exception", e);
        }
        return mval;
    }

    public List<String> pipeLineGet(List<String> keys) {

        List<String> result = new ArrayList<>();
        List<Response<String>> responses = new ArrayList<>(keys.size());
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            Pipeline pipeLine = shardedJedis.pipelined();

            for (String key : keys) {
                responses.add(pipeLine.get(key));
            }
            pipeLine.sync();
            for (Response<String> k : responses) {
                result.add(k.get());
            }
        } catch (Exception e) {
            logger.warn("RedisUtils class pipeLineGet Method is Exception", e);
        }

        return result;
    }

    public void pipeLineSet(List<String> keys) {
        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            Pipeline pipeLine = shardedJedis.pipelined();
            for (String key : keys) {
                pipeLine.set(key, key + 1);
            }
            pipeLine.sync();
        } catch (Exception e) {
            logger.warn("RedisUtils class pipeLineSet Method is Exception", e);
        }
    }

}
