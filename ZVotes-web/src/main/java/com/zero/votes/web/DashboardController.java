package com.zero.votes.web;

import com.zero.votes.beans.UserBean;
import com.zero.votes.persistence.entities.Organizer;
import com.zero.votes.persistence.entities.PollState;
import java.io.Serializable;
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

    public DashboardController() {
    }

    public int getRunningPolls() {
        if (currentOrganizer == null) {
            refreshCurrentOrganizer();
        }
        String[] fieldNames = {"organizers", "pollState"};
        Object[] values_started = {currentOrganizer.getId(), PollState.STARTED};
        int started_polls = pollFacade.countBy(fieldNames, values_started);
        Object[] values_voting = {currentOrganizer.getId(), PollState.VOTING};
        started_polls += pollFacade.countBy(fieldNames, values_voting);
        return started_polls;
    }

    public int getPreparingPolls() {
        if (currentOrganizer == null) {
            refreshCurrentOrganizer();
        }
        String[] fieldNames = {"organizers", "pollState"};
        Object[] values = {currentOrganizer.getId(), PollState.PREPARED};
        return pollFacade.countBy(fieldNames, values);
    }

    public int getFinishedPolls() {
        if (currentOrganizer == null) {
            refreshCurrentOrganizer();
        }
        String[] fieldNames = {"organizers", "pollState"};
        Object[] values = {currentOrganizer.getId(), PollState.FINISHED};
        return pollFacade.countBy(fieldNames, values);
    }

    public void refreshCurrentOrganizer() {
        if (currentOrganizer == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            UserBean userBean = (UserBean) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(context, "#{userBean}", UserBean.class);
            currentOrganizer = userBean.getOrganizer();
        }
    }
}
