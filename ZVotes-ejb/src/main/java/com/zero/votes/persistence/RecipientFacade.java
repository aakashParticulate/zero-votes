package com.zero.votes.persistence;

import com.zero.votes.persistence.entities.Recipient;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class RecipientFacade extends AbstractFacade<Recipient> {

    @PersistenceContext(unitName = "com.zero.votes.ZVotesPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RecipientFacade() {
        super(Recipient.class);
    }

}
