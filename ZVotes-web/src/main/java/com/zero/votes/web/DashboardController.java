package com.zero.votes.web;

import com.zero.votes.beans.UserBean;
import com.zero.votes.persistence.entities.Organizer;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.persistence.entities.PollState;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named("dashboardController")
@SessionScoped
public class DashboardController implements Serializable {

    Organizer currentOrganizer;

    @EJB
    private com.zero.votes.persistence.PollFacade pollFacade;
    @EJB
    private com.zero.votes.persistence.TokenFacade tokenFacade;

    public DashboardController() {
    }

    public List<Poll> getRunningPolls() {
        if (currentOrganizer == null) {
            currentOrganizer = refreshCurrentOrganizer();
        }
        String[] fieldNames = {"organizers", "pollState"};
        Object[] values_published = {currentOrganizer.getId(), PollState.PUBLISHED};
        List<Poll> published_polls = pollFacade.findAllBy(fieldNames, values_published);
        Object[] values_started = {currentOrganizer.getId(), PollState.STARTED};
        published_polls.addAll(pollFacade.findAllBy(fieldNames, values_started));
        Object[] values_voting = {currentOrganizer.getId(), PollState.VOTING};
        published_polls.addAll(pollFacade.findAllBy(fieldNames, values_voting));
        return published_polls;
    }

    public List<Poll> getUnpublishedPolls() {
        if (currentOrganizer == null) {
            currentOrganizer = refreshCurrentOrganizer();
        }
        String[] fieldNames = {"organizers", "pollState"};
        Object[] values = {currentOrganizer.getId(), PollState.PREPARING};
        return pollFacade.findAllBy(fieldNames, values);
    }

    public List<Poll> getFinishedPolls() {
        if (currentOrganizer == null) {
            currentOrganizer = refreshCurrentOrganizer();
        }
        String[] fieldNames = {"organizers", "pollState"};
        Object[] values = {currentOrganizer.getId(), PollState.FINISHED};
        return pollFacade.findAllBy(fieldNames, values);
    }
    
    public int getUsedTokens(Poll poll) {
        String[] fieldNames = {"poll", "used"};
        Object[] values = {poll, true};
        return tokenFacade.countBy(fieldNames, values);
    }

    /**
     * Returns the current organizer.
     */
    public Organizer refreshCurrentOrganizer() {
        FacesContext context = FacesContext.getCurrentInstance();
        UserBean userBean = (UserBean) context.getApplication().evaluateExpressionGet(context, "#{userBean}", UserBean.class);
        return userBean.getOrganizer();
    }
}
