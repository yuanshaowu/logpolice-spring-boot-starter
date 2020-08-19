package com.logpolice.infrastructure.utils;

import com.logpolice.infrastructure.enums.ExecutorFactoryEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * 线程池工厂
 * executorFactory
 *
 * @author huang
 * @date 2019/9/3
 */
@Slf4j
public class LogpoliceExecutorFactory {

    private final Map<ExecutorFactoryEnum, ThreadPoolExecutor> threadPoolExecutorMap;

    public LogpoliceExecutorFactory(Map<ExecutorFactoryEnum, ThreadPoolExecutor> threadPoolExecutorMap) {
        this.threadPoolExecutorMap = threadPoolExecutorMap;
        Arrays.stream(ExecutorFactoryEnum.values()).forEach(this::init);
    }

    private ThreadPoolExecutor init(ExecutorFactoryEnum executorFactoryEnum) {
        ThreadFactory threadFactory = (new BasicThreadFactory.Builder())
                .namingPattern("logpolice-" + executorFactoryEnum.name() + "-thread-%d").build();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(executorFactoryEnum.getCorePoolSize(),
                executorFactoryEnum.getMaximumPoolSize(),
                executorFactoryEnum.getKeepAliveTime(),
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(executorFactoryEnum.getQueueCapacity()),
                threadFactory,
                new ThreadPoolExecutor.DiscardPolicy());
        log.info("LogpoliceExecutorFactory.init threadPoolExecutor success, code:{}", executorFactoryEnum.name());
        return threadPoolExecutorMap.put(executorFactoryEnum, executor);
    }

    public ThreadPoolExecutor getInstance(ExecutorFactoryEnum executorFactoryEnum) {
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(executorFactoryEnum);
        if (Objects.isNull(threadPoolExecutor)) {
            threadPoolExecutor = init(executorFactoryEnum);
        }
        return threadPoolExecutor;
    }

}
