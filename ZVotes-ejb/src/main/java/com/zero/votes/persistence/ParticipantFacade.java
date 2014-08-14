package com.zero.votes.persistence;

import com.zero.votes.persistence.entities.Participant;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ParticipantFacade extends AbstractFacade<Participant> {

    @PersistenceContext(unitName = "com.zero.votes.ZVotesPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParticipantFacade() {
        super(Participant.class);
    }

}
