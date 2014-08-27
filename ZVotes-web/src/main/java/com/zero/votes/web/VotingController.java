package com.zero.votes.web;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.persistence.entities.Item;
import com.zero.votes.persistence.entities.ItemOption;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.persistence.entities.PollState;
import com.zero.votes.persistence.entities.Token;
import com.zero.votes.persistence.entities.Vote;
import com.zero.votes.web.util.ZVotesUtils;
import java.io.Serializable;
import java.util.Date;
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
    @ManagedProperty(value = "#{param.results}")
    private Map<String, Object> results;
    @ManagedProperty(value = "#{param.abstentions}")
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
        updatePollState();
        ZVotesUtils.addInternationalizedInfoMessage("Voted");
        return UrlsPy.HOME.getUrl(true);
    }

    public void updatePollState() {
        if (!current.isPollFinished()) {
            boolean finished;
            PollState state_before = current.getPollState();
            if (current.getEndDate().before(new Date())) {
                finished = true;
            } else {
                finished = true;
                for (Token token : current.getTokens()) {
                    if (!token.isUsed()) {
                        finished = false;
                    }
                }
            }
            if (finished) {
                current.setPollState(PollState.FINISHED);
                pollFacade.edit(current);
            } else if (!finished && !state_before.equals(PollState.VOTING)) {
                current.setPollState(PollState.VOTING);
                pollFacade.edit(current);
            }
        }
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
