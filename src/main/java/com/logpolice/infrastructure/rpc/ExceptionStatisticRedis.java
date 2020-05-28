package com.logpolice.infrastructure.rpc;

import com.alibaba.fastjson.JSONObject;
import com.logpolice.domain.entity.ExceptionStatistic;
import com.logpolice.domain.repository.ExceptionStatisticRepository;
import com.logpolice.infrastructure.enums.NoticeDbTypeEnum;
import com.logpolice.infrastructure.properties.LogpoliceConstant;
import com.logpolice.infrastructure.utils.RedisFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Optional;

/**
 * 异常统计本地缓存
 *
 * @author huang
 * @date 2019/9/11
 */
@Slf4j
public class ExceptionStatisticRedis implements ExceptionStatisticRepository {

    private final List<RedisFactory> redisFactories;

    public ExceptionStatisticRedis(List<RedisFactory> redisFactories) {
        this.redisFactories = redisFactories;
    }

    @Override
    public NoticeDbTypeEnum getType() {
        return NoticeDbTypeEnum.REDIS;
    }

    @Override
    public Optional<ExceptionStatistic> findByOpenId(String openId) {
        ExceptionStatistic exceptionStatistic = null;
        String result = redisFactories.get(0).get(openId);
        if (!Strings.isEmpty(result)) {
            exceptionStatistic = JSONObject.parseObject(result, ExceptionStatistic.class);
        }
        return Optional.ofNullable(exceptionStatistic);
    }

    @Override
    public boolean save(String openId, ExceptionStatistic exceptionStatistic) {
        redisFactories.get(0).setex(openId, JSONObject.toJSONString(exceptionStatistic), LogpoliceConstant.CLEAN_TIME_INTERVAL);
        return true;
    }
}
