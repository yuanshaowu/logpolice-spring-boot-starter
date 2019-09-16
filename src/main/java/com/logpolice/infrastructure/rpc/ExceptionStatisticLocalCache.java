package com.logpolice.infrastructure.rpc;

import com.logpolice.domain.entity.ExceptionStatistic;
import com.logpolice.domain.repository.ExceptionStatisticRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 异常统计本地缓存
 *
 * @author huang
 * @date 2019/9/11
 */
@Slf4j
public class ExceptionStatisticLocalCache implements ExceptionStatisticRepository {

    private Map<String, ExceptionStatistic> checkOpenId;

    public ExceptionStatisticLocalCache(Map<String, ExceptionStatistic> checkOpenId) {
        this.checkOpenId = checkOpenId;
    }

    @Override
    public Optional<ExceptionStatistic> findByOpenId(String openId) {
        ExceptionStatistic source = checkOpenId.get(openId);
        if (Objects.isNull(source)) {
            return Optional.empty();
        }
        ExceptionStatistic target = new ExceptionStatistic();
        BeanUtils.copyProperties(source, target);
        return Optional.of(target);
    }

    @Override
    public void save(String openId, ExceptionStatistic exceptionStatistic) {
        checkOpenId.put(openId, exceptionStatistic);
    }
}
