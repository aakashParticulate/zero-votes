/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zero.votes.persistence;

import com.zero.votes.persistence.entities.Organizer;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author iekadou
 */
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
    
    public Organizer findByUsername(String name) {
            return findBy("username", name);
    }
    
}
