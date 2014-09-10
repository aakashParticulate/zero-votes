package com.zero.votes.cronjobs;

import com.zero.votes.persistence.entities.Poll;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;

@Startup
@Singleton
public class TaskManager {
    
    @Resource(name="java:comp/DefaultManagedScheduledExecutorService")
    ManagedScheduledExecutorService sExecService;
    
    @PreDestroy
    public void destroy() {
        sExecService.shutdownNow();
    }
    
    public void createFinishPollTask(Poll poll) {
        FinishPollTask task = new FinishPollTask(poll);
        sExecService.schedule(task, 10, TimeUnit.SECONDS);
        System.out.println("FINISHED POLL");
    }

}
