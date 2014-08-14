package com.zero.votes.persistence;

import com.zero.votes.persistence.entities.Item;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ItemFacade extends AbstractFacade<Item> {

    @PersistenceContext(unitName = "com.zero.votes.ZVotesPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemFacade() {
        super(Item.class);
    }

}
