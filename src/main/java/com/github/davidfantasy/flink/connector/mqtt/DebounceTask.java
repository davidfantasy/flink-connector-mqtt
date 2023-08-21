package com.github.davidfantasy.flink.connector.mqtt;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 任务节流/防抖
 * 使用场景：在一些频繁触发的业务场景，可能只需要在某段时间内仅执行一次操作，
 * 避免大量任务的重复执行；比如某些错误日志打印，重复的接口调用过滤等
 * @author wany
 */
public class DebounceTask {

    private final ScheduledExecutorService scheduledExecutorService;
    private final Long delay;

    private final Runnable workTask;
    private boolean working;

    DebounceTask(Runnable workTask, Long delay) {
        scheduledExecutorService = new ScheduledThreadPoolExecutor(1, new DebounceScheduleThreadFactory());
        this.delay = delay;
        this.workTask = workTask;
    }

    /**
     * @param runnable 要执行的任务
     * @param delay    延迟执行的时间，单位为毫秒
     * @return 初始化 DebounceTask 对象
     */
    public static DebounceTask build(Runnable runnable, Long delay) {
        return new DebounceTask(runnable, delay);
    }

    /**
     * 在delay时间内如果多次调用doTask，只会在delay结束后执行一次
     */
    public void doTask() {
        if (working) {
            return;
        }
        working = true;
        scheduledExecutorService.schedule(() -> {
            try {
                workTask.run();
            } finally {
                working = false;
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

}

class DebounceScheduleThreadFactory implements ThreadFactory {
    private final ThreadGroup threadGroup = new ThreadGroup("DebounceSchedule");

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(threadGroup, r);
        String threadNamePrefix = "DebounceSchedule";
        t.setName(threadNamePrefix + "-" + t.getId());
        return t;
    }

}
