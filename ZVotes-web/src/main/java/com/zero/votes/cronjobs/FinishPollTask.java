package com.zero.votes.cronjobs;

import com.zero.votes.persistence.PollFacade;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.persistence.entities.PollState;

public class FinishPollTask implements Runnable {
    
    private Poll poll;
    private PollFacade pollFacade;

    public FinishPollTask(Poll poll, PollFacade pollFacade) {
        this.poll = poll;
        this.pollFacade = pollFacade;
    }
    
    @Override
    public void run() {
        System.out.println("YEAH!");
        poll.setPollState(PollState.FINISHED);
        pollFacade.edit(poll);
    }

}
