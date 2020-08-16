package com.logpolice.domain.repository;

import com.logpolice.domain.entity.ExceptionNotice;
import com.logpolice.infrastructure.enums.NoticeSendEnum;

/**
 * 消息仓储层
 *
 * @author huang
 * @date 2019/8/28
 */
public interface ExceptionNoticeRepository {

    /**
     * 获取消息类型
     *
     * @return 消息类型
     */
    NoticeSendEnum getType();

    /**
     * 推送消息
     *
     * @param exceptionNotice 异常信息
     */
    void send(ExceptionNotice exceptionNotice);
}
