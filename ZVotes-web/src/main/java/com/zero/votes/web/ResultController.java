package com.zero.votes.web;

import com.zero.votes.persistence.entities.Item;
import com.zero.votes.persistence.entities.ItemOption;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.persistence.entities.PollState;
import com.zero.votes.persistence.entities.Vote;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
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
        boolean hasWinner = false;
        String winner_name;
        int winnerCount = 0;
        int voteCount = voteFacade.countBy("item", item);
        String[] fieldNamesAbstentions = {"item", "abstention"};
        Object[] valuesAbstentions = {item, true};
        int abstentionCount = voteFacade.countBy(fieldNamesAbstentions, valuesAbstentions);
        for (ItemOption itemOption: item.getOptions()) {
            if (winner != null && itemOption.getVotes().size() == winner.getVotes().size()) {
                hasWinner = false;
            }
            if (winner == null || itemOption.getVotes().size() > winner.getVotes().size()) {
                hasWinner = true;
                winner = itemOption;
                winnerCount = winner.getVotes().size();
            }
        }
        if (!hasWinner) {
            winner_name = "-";
            winnerCount = 0;
        } else {
            winner_name = winner.getShortName();
        }
        HashMap<String, Object> results = new HashMap<>();
        results.put("absolute", winnerCount > (voteCount/2));
        results.put("relative", winnerCount > ((voteCount-abstentionCount)/2));
        results.put("simple", winnerCount > (voteCount-abstentionCount-winnerCount));
        results.put("winner", winner_name);
        return results;
    }
    
    public int getAllVotes() {
        return current.getTokens().size();
    }
    
    /**
     * Takes the current poll + preview token and decides, wether the results
     * are displayed or not.
     * @throws IOException 
     */
    public void checkForInstance() throws IOException {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Long pollId = Long.valueOf((String) req.getParameter("poll"));
        String previewToken = (String) req.getParameter("previewToken");
        String[] fieldNames = {"id", "previewToken", "pollState"};
        Object[] values = {pollId, previewToken, PollState.FINISHED};
        Poll poll = pollFacade.findBy(fieldNames, values);
        if (poll == null) {
            current = null;
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect("/token/");
        }
        String[] fieldNames_token = {"poll", "used"};
        Object[] values_token = {poll, true};
        if (tokenFacade.countBy(fieldNames_token, values_token) < 3) {
            current = null;
        } else {
            current = poll;
        }
        if (current == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect("/token/");
        }
    }
}
