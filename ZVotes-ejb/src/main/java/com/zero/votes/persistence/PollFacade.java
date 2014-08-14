package com.zero.votes.persistence;

import com.zero.votes.persistence.entities.Poll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class PollFacade extends AbstractFacade<Poll> {

    @PersistenceContext(unitName = "com.zero.votes.ZVotesPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PollFacade() {
        super(Poll.class);
    }

}
