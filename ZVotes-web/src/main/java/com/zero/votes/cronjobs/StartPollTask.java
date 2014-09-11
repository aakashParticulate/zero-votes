package com.zero.votes.cronjobs;

import com.zero.votes.persistence.PollFacade;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.persistence.entities.PollState;

/**
 * This task offers functionality to set a poll to STARTED.
 */
public class StartPollTask implements Runnable {
    
    private Poll poll;
    private PollFacade pollFacade;

    public StartPollTask(Poll poll, PollFacade pollFacade) {
        this.poll = poll;
        this.pollFacade = pollFacade;
    }
    
    @Override
    public void run() {
        poll.setPollState(PollState.STARTED);
        pollFacade.edit(poll);
    }

}
