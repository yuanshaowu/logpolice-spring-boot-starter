package com.logpolice.infrastructure.rpc;

import com.logpolice.domain.entity.ExceptionStatistic;
import com.logpolice.domain.repository.ExceptionStatisticRepository;
import com.logpolice.infrastructure.enums.NoticeDbTypeEnum;
import lombok.extern.slf4j.Slf4j;

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

    private final Map<String, ExceptionStatistic> exceptionStatisticMap;

    public ExceptionStatisticLocalCache(Map<String, ExceptionStatistic> exceptionStatisticMap) {
        this.exceptionStatisticMap = exceptionStatisticMap;
    }

    @Override
    public NoticeDbTypeEnum getType() {
        return NoticeDbTypeEnum.LOCAL_CACHE;
    }

    @Override
    public Optional<ExceptionStatistic> findByOpenId(String openId) {
        ExceptionStatistic exceptionStatistic = exceptionStatisticMap.get(openId);
        if (Objects.isNull(exceptionStatistic)) {
            return Optional.empty();
        }
        return Optional.of(new ExceptionStatistic(exceptionStatistic.getOpenId(),
                exceptionStatistic.getShowCount(),
                exceptionStatistic.getLastShowedCount(),
                exceptionStatistic.getFirstTime(),
                exceptionStatistic.getNoticeTime()));
    }

    @Override
    public boolean save(String openId, ExceptionStatistic exceptionStatistic) {
        exceptionStatisticMap.put(openId, exceptionStatistic);
        return true;
    }
}
