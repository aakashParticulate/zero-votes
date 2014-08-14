package com.zero.votes.persistence;

import com.zero.votes.persistence.entities.RecipientList;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class RecipientListFacade extends AbstractFacade<RecipientList> {

    @PersistenceContext(unitName = "com.zero.votes.ZVotesPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RecipientListFacade() {
        super(RecipientList.class);
    }

}
