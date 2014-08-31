/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zero.votes.persistence;

import com.zero.votes.persistence.entities.Item;
import com.zero.votes.persistence.entities.Organizer;
import java.util.List;
import javax.ejb.embeddable.EJBContainer;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * Integration tests are supposed to test the behavior with respect to
 * dependencies. That's why an EntityManagerFactory has to be created
 * and not mocked like in Unit-Tests instead.
 * 
 * http://www.oracle.com/technetwork/articles/java/integrationtesting-487452.html
 */
public class ItemFacadeIT {
    
    private ItemFacade itemFacade;
    private EntityTransaction transaction;
    
    @Before
    public void initializeDependencies(){
        itemFacade = new ItemFacade();
        itemFacade.em = Persistence.createEntityManagerFactory("integration").createEntityManager();
        this.transaction = itemFacade.em.getTransaction();
    }

    /**
     * Test of create method, of class OrganizerFacade.
     */
    @Test
    public void testCreate() throws Exception {
        System.out.println("create");
        Item entity = new Item();
        entity.setTitle("testItem");
        transaction.begin();
        itemFacade.create(entity);
        transaction.commit();
        assertEquals(1,itemFacade.count());
    }
    
}
