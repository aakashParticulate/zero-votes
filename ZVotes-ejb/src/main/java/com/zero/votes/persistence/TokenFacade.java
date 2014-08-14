package com.zero.votes.persistence;

import com.zero.votes.persistence.entities.Token;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TokenFacade extends AbstractFacade<Token> {

    @PersistenceContext(unitName = "com.zero.votes.ZVotesPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TokenFacade() {
        super(Token.class);
    }

}
