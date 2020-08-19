package com.logpolice.infrastructure.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huang
 * @date 2020/8/19
 */
@Getter
@AllArgsConstructor
public enum ExecutorFactoryEnum {

    /**
     * NOTIFY：消息推送
     */
    NOTIFY(50, 50, 0, 3000);

    private int corePoolSize;
    private int maximumPoolSize;
    private int keepAliveTime;
    private int queueCapacity;

}
