package com.logpolice.infrastructure.rpc;

import com.logpolice.infrastructure.enums.NoticeDbTypeEnum;
import com.logpolice.infrastructure.utils.LockUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * jvm锁
 *
 * @author huang
 * @date 2019/9/3
 */
public class LockUtilsLocalCache implements LockUtils {

    private final Map<String, AtomicInteger> versionMap;

    public LockUtilsLocalCache(Map<String, AtomicInteger> versionMap) {
        this.versionMap = versionMap;
    }

    @Override
    public NoticeDbTypeEnum getType() {
        return NoticeDbTypeEnum.LOCAL_CACHE;
    }

    @Override
    public boolean lock(String key) {
        AtomicInteger version = versionMap.computeIfAbsent(key, v -> new AtomicInteger(1));
        return Objects.equals(version.incrementAndGet(), 1);
    }

    @Override
    public void unlock(String key) {
        AtomicInteger version = versionMap.get(key);
        if (Objects.nonNull(version)) {
            version.set(0);
        }
    }
}
