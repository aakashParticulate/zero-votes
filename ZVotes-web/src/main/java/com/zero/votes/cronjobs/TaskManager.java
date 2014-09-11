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

/**
 * Creates and manages tasks, which have to be scheduled and executed
 * in sExecService.
 */
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
    
    public void createStartedMailTask(Poll poll, EMailer eMailer, Locale locale, String url) {
        StartedMailTask task = new StartedMailTask(poll, eMailer, locale, url);
        
        DateTime now = new DateTime();
        Seconds deltaSeconds = Seconds.secondsBetween(now, new DateTime(poll.getStartDate()));

        sExecService.schedule(task, deltaSeconds.getSeconds(), TimeUnit.SECONDS);
    }
    
    public void createReminderMailTask(Poll poll, EMailer eMailer, Locale locale, String url) {
        ReminderMailTask task = new ReminderMailTask(poll, eMailer, locale, url);
        
        DateTime now = new DateTime();
        DateTime remindDate = new DateTime(poll.getEndDate()).minusDays(1);
        Seconds deltaSeconds = Seconds.secondsBetween(now, remindDate);
        
        // only send reminder emails if remind date is at least one day in the future
        if (deltaSeconds.getSeconds() > 86400) {
            sExecService.schedule(task, deltaSeconds.getSeconds(), TimeUnit.SECONDS);
        }
    }

}
