//package com.zero.votes.cronjobs;
//
//import com.zero.votes.persistence.entities.Poll;
//import com.zero.votes.persistence.entities.PollState;
//import javax.ejb.EJB;
//
//
//public class FinishPollJob implements Runnable {
//    private final Poll poll;
//    
//    @EJB
//    private com.zero.votes.persistence.PollFacade pollFacade;
//
//    public FinishPollJob(Poll poll) {
//        this.poll = poll;
//    }
//    
//    @Override
//    public void run() {
//        poll.setPollState(PollState.FINISHED);
//        pollFacade.edit(poll);
//    }
//}
