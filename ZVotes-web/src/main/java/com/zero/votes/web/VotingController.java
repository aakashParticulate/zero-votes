package com.zero.votes.web;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.persistence.entities.Poll;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named("votingController")
@SessionScoped
public class VotingController implements Serializable {

    private Poll current;
    @EJB
    private com.zero.votes.persistence.PollFacade pollFacade;
    

    public VotingController() {
    }

    public Poll getCurrent() {
        return current;
    }

    public void setCurrent(Poll current) {
        this.current = current;
    }

}
