package com.logpolice.domain.repository;

import com.logpolice.domain.entity.ExceptionStatistic;

import java.util.Optional;

/**
 * 异常统计仓储层
 *
 * @author huang
 * @date 2019/8/28
 */
public interface ExceptionStatisticRepository {

    /**
     * 查询异常统计
     *
     * @param openId 唯一标识
     * @return 异常统计
     */
    Optional<ExceptionStatistic> findByOpenId(String openId);

    /**
     * 保存异常统计
     *
     * @param openId             唯一标识
     * @param exceptionStatistic 异常统计
     */
    void save(String openId, ExceptionStatistic exceptionStatistic);
}
