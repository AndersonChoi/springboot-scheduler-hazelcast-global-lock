package com.hazelcast.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final String LOCK_NAME = "SPRING_BOOT_SCHEDULER_LOCK";


    @Autowired
    @Qualifier("hazelcastInstance")
    private HazelcastInstance hazelcastInstance;

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {

        Lock iLock = hazelcastInstance.getLock(LOCK_NAME);
        try {
            if (iLock.tryLock(10, TimeUnit.SECONDS)) {
                executeJob();
            } else {
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        } finally {
            iLock.unlock();
        }
    }

    private void executeJob() {
        log.info("The time is now {}", dateFormat.format(new Date()));
    }
}
