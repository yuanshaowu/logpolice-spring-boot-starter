package com.logpolice.domain.repository;

import com.logpolice.domain.entity.ExceptionStatistic;
import com.logpolice.infrastructure.enums.NoticeRepositoryEnum;

import java.util.Optional;

/**
 * 异常统计仓储层
 *
 * @author huang
 * @date 2019/8/28
 */
public interface ExceptionStatisticRepository {

    /**
     * 获取仓储类型
     *
     * @return 仓储类型
     */
    NoticeRepositoryEnum getType();

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
     * @param version            版本号
     * @param exceptionStatistic 异常统计
     */
    boolean save(String openId, String version, ExceptionStatistic exceptionStatistic);
}
