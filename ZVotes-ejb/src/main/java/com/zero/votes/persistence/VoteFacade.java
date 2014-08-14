package com.zero.votes.persistence;

import com.zero.votes.persistence.entities.Vote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class VoteFacade extends AbstractFacade<Vote> {

    @PersistenceContext(unitName = "com.zero.votes.ZVotesPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VoteFacade() {
        super(Vote.class);
    }

}
