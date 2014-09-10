package com.zero.votes.web;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.persistence.entities.Item;
import com.zero.votes.persistence.entities.ItemOption;
import com.zero.votes.persistence.entities.ItemType;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.persistence.entities.PollState;
import com.zero.votes.persistence.entities.Token;
import com.zero.votes.persistence.entities.Vote;
import com.zero.votes.web.util.ZVotesUtils;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@Named("votingController")
@SessionScoped
public class VotingController implements Serializable {

    private Poll current;
    private Token token;
    @ManagedProperty(value = "#{param.results}")
    private Map<String, Object> results;
    @ManagedProperty(value = "#{param.abstentions}")
    private Map<String, Object> abstentions;
    @ManagedProperty(value = "#{param.freeTexts}")
    private Map<String, Object> freeTexts;
    @ManagedProperty(value = "#{param.freeTextDescriptions}")
    private Map<String, Object> freeTextDescriptions;
    @EJB
    private com.zero.votes.persistence.PollFacade pollFacade;
    @EJB
    private com.zero.votes.persistence.VoteFacade voteFacade;
    @EJB
    private com.zero.votes.persistence.TokenFacade tokenFacade;
    @EJB
    private com.zero.votes.persistence.ItemOptionFacade itemOptionFacade;
    @EJB
    private com.zero.votes.persistence.ParticipantFacade participantFacade;
    @Inject
    private com.zero.votes.web.TokenController tokenController;

    public VotingController() {
    }

    public String prepareVoting(Poll poll, Token token) {
        this.current = poll;
        this.token = token;
        results = new HashMap<>();
        abstentions = new HashMap<>();
        freeTexts = new HashMap<>();
        freeTextDescriptions = new HashMap<>();
        for (Item item : current.getItems()) {
            abstentions.put(item.getId().toString(), Boolean.TRUE);
            for (ItemOption itemOption : item.getOptions()) {
                results.put(itemOption.getId().toString(), Boolean.FALSE);
            }
        }
        return getCurrentPollUrl();
    }
    
    private String getCurrentPollUrl() {
        String tokenId;
        String currentId;
        if (token != null) {
            tokenId = token.getId().toString();
        } else {
            tokenId = "";
        }
        if (current != null) {
            currentId = current.getId().toString();
        } else {
            currentId = "";
        }
        return UrlsPy.POLL.getUrl(false)+"?token="+tokenId+"&poll="+currentId;
    }

    public Poll getCurrent() {
        return current;
    }

    public void setCurrent(Poll current) {
        this.current = current;
    }

    public String abstainAll() {
        for (Item item : current.getItems()) {
            abstentions.remove(item.getId().toString());
            abstentions.put(item.getId().toString(), Boolean.TRUE);
            for (ItemOption itemOption : item.getOptions()) {
                results.remove(itemOption.getId().toString());
                results.put(itemOption.getId().toString(), Boolean.FALSE);
            }
        }
        return this.submit();
    }

    public String submit() {
        if (this.token == null) {
            ZVotesUtils.addInternationalizedWarnMessage("NoValidTokenActive");
            return getCurrentPollUrl();
        }
        if (this.token.isUsed()) {
            ZVotesUtils.addInternationalizedWarnMessage("TokenAlreadyUsed");
            return getCurrentPollUrl();
        }
        for (Item item : current.getItems()) {
            int votes = 0;
            for (ItemOption itemOption : item.getOptions()) {
                if (results.get(itemOption.getId().toString()) == Boolean.TRUE) {
                    votes++;
                }
            }
            if (item.isOwnOptions()) {
                for (int i = 1; i <= this.getMaxOptions(item); i++) {
                    String freeTextId = item.getId().toString()+"_"+Integer.toString(i);
                    if (results.get(freeTextId) == Boolean.TRUE) {
                        votes++;
                    }
                    if (itemOptionFacade.countBy("shortName", freeTexts.get(freeTextId)) > 0) {
                        ZVotesUtils.addInternationalizedErrorMessage("ShortNameAlreadyUsed");
                        return getCurrentPollUrl();
                    }
                }
            }
            if (votes > item.getM() && item.getType().equals(ItemType.M_OF_N)) {
                return getCurrentPollUrl();
            } else if (votes > 1 && !item.getType().equals(ItemType.M_OF_N)) {
                return getCurrentPollUrl();
            }
        }
        for (Item item : current.getItems()) {
            if (abstentions.get(item.getId().toString()) == Boolean.TRUE) {
                Vote vote = new Vote();
                vote.setItem(item);
                vote.setAbstention(true);
                voteFacade.create(vote);
            } else {
                if (item.isOwnOptions()) {
                    for (int i = 1; i <= this.getMaxOptions(item); i++) {
                        String freeTextId = item.getId().toString()+"_"+Integer.toString(i);
                        if (results.get(freeTextId) == Boolean.TRUE) {
                            ItemOption itemOption = new ItemOption();
                            itemOption.setShortName((String) freeTexts.get(freeTextId));
                            itemOption.setDescription((String) freeTextDescriptions.get(freeTextId));
                            itemOption.setItem(item);
                            itemOptionFacade.create(itemOption);
                            Vote vote = new Vote();
                            vote.setItem(item);
                            vote.setItemOption(itemOption);
                            vote.setAbstention(false);
                            voteFacade.create(vote);
                        }
                    }
                }
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
            for (ItemOption itemOption : item.getOptions()) {
                results.remove(itemOption.getId().toString());
                results.put(itemOption.getId().toString(), Boolean.FALSE);
            }
            abstentions.remove(item.getId().toString());
            abstentions.put(item.getId().toString(), Boolean.TRUE);
        }
        tokenController.markUsed(token);
        if (current.isParticipationTracking()) {
            token.getParticipant().setHasVoted(true);
            participantFacade.edit(token.getParticipant());
        }
        updatePollState();
        this.token = null;
        this.current = null;
        freeTexts = new HashMap<>();
        freeTextDescriptions = new HashMap<>();
        ZVotesUtils.addInternationalizedInfoMessage("Voted");
        return UrlsPy.HOME.getUrl(true);
    }
    
    public String cancel() {
        for (Item item : current.getItems()) {
            for (ItemOption itemOption : item.getOptions()) {
                results.remove(itemOption.getId().toString());
                results.put(itemOption.getId().toString(), Boolean.FALSE);
            }
            abstentions.remove(item.getId().toString());
            abstentions.put(item.getId().toString(), Boolean.TRUE);
        }
        freeTexts = new HashMap<>();
        freeTextDescriptions = new HashMap<>();
        this.token = null;
        this.current = null;
        return UrlsPy.TOKEN.getUrl(true);
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

    public int getMaxOptions(Item item) {
        if (item.getType().equals(ItemType.M_OF_N)) {
            return item.getM();
        } else {
            return 1;
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

    public Map<String, Object> getFreeTexts() {
        return freeTexts;
    }

    public void setFreeTexts(Map<String, Object> freeTexts) {
        this.freeTexts = freeTexts;
    }

    public Map<String, Object> getFreeTextDescriptions() {
        return freeTextDescriptions;
    }

    public void setFreeTextDescriptions(Map<String, Object> freeTextDescriptions) {
        this.freeTextDescriptions = freeTextDescriptions;
    }
    
    public void checkForInstance() throws IOException {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        if (req.getParameter("token") != null && !req.getParameter("token").isEmpty()) {
            token = tokenFacade.find(Long.valueOf(req.getParameter("token")));
        } else {
            token = null;
        }
        if (req.getParameter("poll") != null && !req.getParameter("poll").isEmpty()) {
            current = pollFacade.find(Long.valueOf(req.getParameter("poll")));
        } else {
            current = null;
        }
        if (current == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect("/token/");
        }
    }

}
