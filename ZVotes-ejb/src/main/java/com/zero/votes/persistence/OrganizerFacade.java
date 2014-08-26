package com.zero.votes.persistence;

import com.zero.votes.persistence.entities.Organizer;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class OrganizerFacade extends AbstractFacade<Organizer> {

    @PersistenceContext(unitName = "com.zero.votes.ZVotesPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OrganizerFacade() {
        super(Organizer.class);
    }

}
