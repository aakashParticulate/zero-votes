package com.zero.votes.web;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.persistence.entities.Item;
import com.zero.votes.persistence.entities.ItemOption;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.persistence.entities.Vote;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.inject.Inject;
import javax.inject.Named;

@Named("votingController")
@SessionScoped
public class VotingController implements Serializable {

    private Poll current;
    @ManagedProperty(value="#{param.results}")
    private Map<String, Object> results;
    @ManagedProperty(value="#{param.abstentions}")
    private Map<String, Object> abstentions;
    @EJB
    private com.zero.votes.persistence.PollFacade pollFacade;
    @EJB
    private com.zero.votes.persistence.VoteFacade voteFacade;
    @Inject
    private com.zero.votes.web.TokenController tokenController;
    

    public VotingController() {
    }
    
    public String prepareVoting(Poll poll) {
        current = poll;
        results = new HashMap<String, Object>();
        abstentions = new HashMap<String, Object>();
        for (Item item : current.getItems()) {
            abstentions.put(item.getId().toString(), Boolean.FALSE);
            for (ItemOption itemOption : item.getOptions()) {
                results.put(itemOption.getId().toString(), Boolean.FALSE);
            }
        }

        return UrlsPy.POLL.getUrl(true);
    }

    public Poll getCurrent() {
        return current;
    }

    public void setCurrent(Poll current) {
        this.current = current;
    }
    
    public String submit() {
        for (Item item : current.getItems()) {
            if (abstentions.get(item.getId().toString()) == Boolean.TRUE) {
                Vote vote = new Vote();
                vote.setItem(item);
                vote.setAbstention(true);
                voteFacade.create(vote);
            } else {
                for (ItemOption itemOption : item.getOptions()) {
                    if (results.get(itemOption.getId().toString()) == Boolean.TRUE) {
                        Vote vote = new Vote();
                        vote.setItem(item);
                        vote.setItemOption(itemOption);
                        vote.setAbstention(false);
                        voteFacade.create(vote);
                    }
                }
            }
        }
        tokenController.markUsed();
        return UrlsPy.HOME.getUrl(true);
    }

    public Map<String, Object> getResults() {
        return results;
    }

    public void setResults(Map<String, Object> results) {
        this.results = results;
    }

    public Map<String, Object> getAbstentions() {
        return abstentions;
    }

    public void setAbstentions(Map<String, Object> abstentions) {
        this.abstentions = abstentions;
    }

}
