package com.zero.votes.web;

import com.zero.votes.beans.UserBean;
import com.zero.votes.persistence.entities.Organizer;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.persistence.entities.PollState;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named("adminDashboardController")
@SessionScoped
public class AdminDashboardController implements Serializable {

    @EJB
    private com.zero.votes.persistence.PollFacade pollFacade;
    @EJB
    private com.zero.votes.persistence.TokenFacade tokenFacade;

    public AdminDashboardController() {
    }

    public List<Poll> getRunningPolls() {
        String[] fieldNames = {"pollState"};
        Object[] values_started = {PollState.STARTED};
        List<Poll> started_polls = pollFacade.findAllBy(fieldNames, values_started);
        Object[] values_voting = {PollState.VOTING};
        started_polls.addAll(pollFacade.findAllBy(fieldNames, values_voting));
        return started_polls;
    }

    public List<Poll> getUnpublishedPolls() {
        String[] fieldNames = {"pollState"};
        Object[] values = {PollState.PREPARED};
        return pollFacade.findAllBy(fieldNames, values);
    }

    public List<Poll> getFinishedPolls() {
        String[] fieldNames = {"pollState"};
        Object[] values = {PollState.FINISHED};
        return pollFacade.findAllBy(fieldNames, values);
    }
    
    public int getUsedTokens(Poll poll) {
        String[] fieldNames = {"poll", "used"};
        Object[] values = {poll, true};
        return tokenFacade.countBy(fieldNames, values);
    }
}
