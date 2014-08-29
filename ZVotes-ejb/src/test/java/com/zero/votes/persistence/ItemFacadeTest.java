/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zero.votes.persistence;

import com.zero.votes.persistence.entities.Item;
import java.util.List;
import javax.ejb.embeddable.EJBContainer;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 * @author Marcel
 */
public class ItemFacadeTest {
    
    private ItemFacade itemFacade;
    
    private EntityManagerFactory emf;
    private Cache c;
    
    @Before
    public void setUp() {
        itemFacade = new ItemFacade();
        
        //mocking an EntityManager-object
        itemFacade.em = mock(EntityManager.class);
        
        //mocking an EntityManagerFactory-object
        emf = mock(EntityManagerFactory.class);
        //setting up replacing getter
        doReturn( emf ).when( itemFacade.em ).getEntityManagerFactory();
        
        //mocking a Cache-object
        c = mock(Cache.class);
        doReturn( c ).when( emf ).getCache();
    }
    
    /**
     * Test of create method, of class ItemFacade.
     */
    @Test
    public void testCreate() {
        System.out.println("create");
        Item itemEntity = new Item();
        this.itemFacade.create(itemEntity);
        verify(itemFacade.em, times(1)).persist(itemEntity);
    }
}
