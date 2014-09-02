package com.zero.votes.cronjobs;

import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.persistence.entities.PollState;
import java.util.Date;
import java.util.HashMap;
import javax.ejb.EJB;

public class RecoverTasks implements Runnable {
    @EJB
    private com.zero.votes.persistence.PollFacade pollFacade;
    @EJB
    private ZVotesScheduler zVotesScheduler;
    
    @Override
    public void run() {
        // In Case of Server not running while finish Task
        for (Poll poll: pollFacade.findAllBy("pollState", PollState.STARTED)) {
            HashMap<Date, Runnable> finishTask = new HashMap<>();
            finishTask.put(poll.getEndDate(), new FinishPollJob(poll));
            zVotesScheduler.addTask("FINISH_POLL_"+poll.getId(), finishTask);
        }
    }
}
