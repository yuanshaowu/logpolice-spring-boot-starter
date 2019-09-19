package com.logpolice.infrastructure.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 消息推送类型
 *
 * @author huang
 * @date 2019/8/28
 */
@Getter
@AllArgsConstructor
public enum NoticeRepositoryEnum {

    /**
     * LOCAL_CACHE：本地
     * REDIS：redis
     */
    LOCAL_CACHE(false),
    REDIS(true);

    private Boolean enableRedisStorage;

    public static NoticeRepositoryEnum getType(Boolean enableRedisStorage) {
        return Arrays.stream(NoticeRepositoryEnum.values())
                .filter(n -> Objects.equals(n.getEnableRedisStorage(), enableRedisStorage)).findFirst()
                .orElse(NoticeRepositoryEnum.LOCAL_CACHE);
    }
}
