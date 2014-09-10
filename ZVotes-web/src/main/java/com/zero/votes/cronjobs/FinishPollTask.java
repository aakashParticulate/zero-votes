package com.zero.votes.cronjobs;

import com.zero.votes.persistence.PollFacade;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.persistence.entities.PollState;
import javax.ejb.EJB;

public class FinishPollTask implements Runnable {
    private final Poll poll;
    
    @EJB
    private PollFacade pollFacade;

    public FinishPollTask(Poll poll) {
        this.poll = poll;
    }
    
    @Override
    public void run() {
        System.out.println("YEAH!");
        poll.setPollState(PollState.FINISHED);
        pollFacade.edit(poll);
    }

}
