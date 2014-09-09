package com.zero.votes.web;

import com.zero.votes.persistence.entities.Item;
import com.zero.votes.persistence.entities.ItemOption;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.persistence.entities.PollState;
import com.zero.votes.persistence.entities.Vote;
import java.io.Serializable;
import java.util.HashMap;
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
    @EJB
    private com.zero.votes.persistence.ItemOptionFacade itemOptionFacade;
    

    public ResultController() {
    }
    
    @PostConstruct
    public void getPollFromUrl() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Long pollId = Long.valueOf((String) req.getParameter("poll"));
        String[] fieldNames = {"id", "pollState"};
        Object[] values = {pollId, PollState.FINISHED};
        Poll poll = pollFacade.findBy(fieldNames, values);
        if (poll == null) {
            return;
        }
        String[] fieldNames_token = {"poll", "used"};
        Object[] values_token = {poll, true};
        if (tokenFacade.countBy(fieldNames_token, values_token) < 3) {
            current = null;
        } else {
            current = poll;
        }
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
    
    public int getUsedTokens() {
        String[] fieldNames = {"poll", "used"};
        Object[] values = {current, true};
        return tokenFacade.countBy(fieldNames, values);
    }
    
    public List<Vote> getAbstentions(Item item) {
        String[] fieldNames = {"item", "abstention"};
        Object[] values = {item, true};
        return voteFacade.findAllBy(fieldNames, values);
    }
    
    public HashMap<String, Object> getWinner(Item item) {
        ItemOption winner = null;
        int winnerCount = 0;
        int voteCount = voteFacade.countBy("item", item);
        String[] fieldNamesAbstentions = {"item", "abstention"};
        Object[] valuesAbstentions = {item, true};
        int abstentionCount = voteFacade.countBy(fieldNamesAbstentions, valuesAbstentions);
        for (ItemOption itemOption: item.getOptions()) {
            if (winner == null || itemOption.getVotes().size() > winner.getVotes().size()) {
                winner = itemOption;
                winnerCount = winner.getVotes().size();
            }
        }
        HashMap<String, Object> results = new HashMap<String, Object>();
        results.put("absolute", winnerCount > (voteCount/2));
        results.put("relative", winnerCount > ((voteCount-abstentionCount)/2));
        results.put("simple", winnerCount > (voteCount-abstentionCount-winnerCount));
        results.put("winner", winner);
        return results;
    }
    
    public int getAllVotes() {
        return current.getTokens().size();
    }
}
