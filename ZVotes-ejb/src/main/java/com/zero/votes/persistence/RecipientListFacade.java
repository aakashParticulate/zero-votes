/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zero.votes.persistence;

import com.zero.votes.persistence.entities.RecipientList;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author iekadou
 */
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