package com.zero.votes.persistence;

import com.zero.votes.persistence.entities.ItemOption;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ItemOptionFacade extends AbstractFacade<ItemOption> {

    @PersistenceContext(unitName = "com.zero.votes.ZVotesPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemOptionFacade() {
        super(ItemOption.class);
    }

}
