package com.zero.votes.web;

import com.zero.votes.persistence.entities.Item;
import com.zero.votes.persistence.entities.ItemOption;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.persistence.entities.Vote;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@Named("resultController")
@SessionScoped
public class ResultController implements Serializable {

    private Poll current;
    @EJB
    private com.zero.votes.persistence.PollFacade pollFacade;
    @EJB
    private com.zero.votes.persistence.VoteFacade voteFacade;
    @EJB
    private com.zero.votes.persistence.TokenFacade tokenFacade;
    

    public ResultController() {
    }
    
    @PostConstruct
    public void getPollFromUrl() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Long pollId = Long.valueOf((String) req.getParameter("poll"));
        current = pollFacade.find(pollId);
    }

    public Poll getCurrent() {
        return current;
    }

    public void setCurrent(Poll current) {
        this.current = current;
    }
    
    public List<Vote> getVotes(ItemOption itemOption) {
        return voteFacade.findAllBy("itemOption", itemOption);
    }
    
    public List<Vote> getAbstentions(Item item) {
        String[] fieldNames = {"item", "abstention"};
        Object[] values = {item, true};
        return voteFacade.findAllBy(fieldNames, values);
    }
    
    public int getAllVotes() {
        return current.getTokens().size();
    }
}
