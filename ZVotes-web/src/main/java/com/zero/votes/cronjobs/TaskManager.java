package com.zero.votes.cronjobs;

import com.zero.votes.persistence.PollFacade;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.web.util.EMailer;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

@Startup
@Singleton
public class TaskManager {
    
    @Resource(name="java:comp/DefaultManagedScheduledExecutorService")
    ManagedScheduledExecutorService sExecService;
    
    @PreDestroy
    public void destroy() {
        sExecService.shutdownNow();
    }
    
    public void createStartPollTask(Poll poll, PollFacade pollFacade) {
        StartPollTask task = new StartPollTask(poll, pollFacade);
        
        DateTime now = new DateTime();
        Seconds deltaSeconds = Seconds.secondsBetween(now, new DateTime(poll.getStartDate()));

        sExecService.schedule(task, deltaSeconds.getSeconds(), TimeUnit.SECONDS);
    }
    
    public void createFinishPollTask(Poll poll, PollFacade pollFacade) {
        FinishPollTask task = new FinishPollTask(poll, pollFacade);
        
        DateTime now = new DateTime();
        Seconds deltaSeconds = Seconds.secondsBetween(now, new DateTime(poll.getEndDate()));

        sExecService.schedule(task, deltaSeconds.getSeconds(), TimeUnit.SECONDS);
    }
    
    public void createStartedMailTask(Poll poll, EMailer eMailer, Locale locale) {
        StartedMailTask task = new StartedMailTask(poll, eMailer, locale);
        
        DateTime now = new DateTime();
        Seconds deltaSeconds = Seconds.secondsBetween(now, new DateTime(poll.getStartDate()));

        sExecService.schedule(task, deltaSeconds.getSeconds(), TimeUnit.SECONDS);
    }
    
    public void createReminderMailTask(Poll poll, EMailer eMailer, Locale locale) {
        ReminderMailTask task = new ReminderMailTask(poll, eMailer, locale);
        
        DateTime now = new DateTime();
        DateTime remindDate = new DateTime(poll.getEndDate()).minusDays(1);
        Seconds deltaSeconds = Seconds.secondsBetween(now, remindDate);

        sExecService.schedule(task, deltaSeconds.getSeconds(), TimeUnit.SECONDS);
    }

}
