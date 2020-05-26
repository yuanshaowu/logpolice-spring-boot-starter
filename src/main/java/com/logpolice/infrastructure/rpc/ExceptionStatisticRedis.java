package com.logpolice.infrastructure.rpc;

import com.alibaba.fastjson.JSONObject;
import com.logpolice.domain.entity.ExceptionStatistic;
import com.logpolice.domain.repository.ExceptionStatisticRepository;
import com.logpolice.infrastructure.enums.NoticeDbTypeEnum;
import com.logpolice.infrastructure.properties.LogpoliceConstant;
import com.logpolice.infrastructure.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.Optional;

/**
 * 异常统计本地缓存
 *
 * @author huang
 * @date 2019/9/11
 */
@Slf4j
public class ExceptionStatisticRedis implements ExceptionStatisticRepository {

    private final RedisUtils redisUtils;

    public ExceptionStatisticRedis(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    @Override
    public NoticeDbTypeEnum getType() {
        return NoticeDbTypeEnum.REDIS;
    }

    @Override
    public Optional<ExceptionStatistic> findByOpenId(String openId) {
        ExceptionStatistic exceptionStatistic = null;
        String result = redisUtils.get(openId);
        if (!Strings.isEmpty(result)) {
            exceptionStatistic = JSONObject.parseObject(result, ExceptionStatistic.class);
        }
        return Optional.ofNullable(exceptionStatistic);
    }

    @Override
    public boolean save(String openId, ExceptionStatistic exceptionStatistic) {
        redisUtils.setex(openId, LogpoliceConstant.CLEAN_TIME_INTERVAL, JSONObject.toJSONString(exceptionStatistic));
        return true;
    }
}
