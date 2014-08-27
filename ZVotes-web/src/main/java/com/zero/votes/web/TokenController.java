package com.zero.votes.web;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.persistence.entities.Token;
import com.zero.votes.web.util.ZVotesUtils;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.inject.Inject;
import javax.inject.Named;

@Named("tokenController")
@SessionScoped
public class TokenController implements Serializable {

    @ManagedProperty(value = "#{param.tokenString}")
    private String tokenString;
    private Token current;
    @EJB
    private com.zero.votes.persistence.TokenFacade tokenFacade;
    @Inject
    private com.zero.votes.web.VotingController votingController;

    public TokenController() {
    }
    
    public void markUsed() {
        current.setUsed(true);
        tokenFacade.edit(current);
    }

    public String submit() {
        String[] fieldNames = {"tokenString"};
        Object[] values = {tokenString};
        Token token = tokenFacade.findBy(fieldNames, values);
        if (token == null) {
            ZVotesUtils.addInternationalizedErrorMessage("TokenNotFound");
            return UrlsPy.TOKEN.getUrl();
        } else {
            if (token.isUsed()) {
                ZVotesUtils.addInternationalizedErrorMessage("TokenAlreadyUsed");
                return UrlsPy.TOKEN.getUrl();
            } else {
                if (token.getPoll().isPollFinished()) {
                    ZVotesUtils.addInternationalizedErrorMessage("PollAlreadyFinished");
                    return votingController.prepareVoting(current.getPoll());
                } else {
                    current = token;
                    return votingController.prepareVoting(current.getPoll());
                }
            }
        }
    }

    public String getTokenString() {
        return tokenString;
    }

    public void setTokenString(String tokenString) {
        this.tokenString = tokenString;
    }

}
