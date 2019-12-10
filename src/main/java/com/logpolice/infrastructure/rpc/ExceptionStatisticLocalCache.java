package com.logpolice.infrastructure.rpc;

import com.logpolice.domain.entity.ExceptionStatistic;
import com.logpolice.domain.repository.ExceptionStatisticRepository;
import com.logpolice.infrastructure.enums.NoticeRepositoryEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异常统计本地缓存
 *
 * @author huang
 * @date 2019/9/11
 */
@Slf4j
public class ExceptionStatisticLocalCache implements ExceptionStatisticRepository {

    private final int VERSION_MAX = 20;

    private final Map<String, ExceptionStatistic> exceptionStatisticMap;
    private final Map<String, AtomicInteger> exceptionVersionMap;

    public ExceptionStatisticLocalCache(Map<String, ExceptionStatistic> exceptionStatisticMap,
                                        Map<String, AtomicInteger> exceptionVersionMap) {
        this.exceptionStatisticMap = exceptionStatisticMap;
        this.exceptionVersionMap = exceptionVersionMap;
    }

    @Override
    public NoticeRepositoryEnum getType() {
        return NoticeRepositoryEnum.LOCAL_CACHE;
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
        boolean lock;
        if (lock = lock(openId)) {
            exceptionStatisticMap.put(openId, exceptionStatistic);
            unlock(openId);
        }
        return lock;
    }

    private boolean lock(String openId) {
        AtomicInteger version = exceptionVersionMap.computeIfAbsent(openId, v -> new AtomicInteger(0));
        int remoteVersion = version.incrementAndGet();
        if (remoteVersion == 1) {
            return true;
        }
        if (remoteVersion > VERSION_MAX) {
            exceptionVersionMap.remove(openId);
        }
        return false;
    }

    private void unlock(String openId) {
        exceptionVersionMap.remove(openId);
    }
}
