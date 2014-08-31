/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zero.votes.persistence;

import com.zero.votes.persistence.entities.Item;
import java.lang.reflect.Field;
import java.util.List;
import javax.ejb.embeddable.EJBContainer;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 * These tests only verify wether something was called.
 * No actual value tests are possible with mocks.
 * 
 * http://www.oracle.com/technetwork/articles/java/unittesting-455385.html
 */
public class ItemFacadeTest {
    
    private ItemFacade itemFacade;
    //private EntityManager em;
    private EntityManagerFactory emf;
    private Cache c;
    
    @Before
    public void setUp() throws Exception{
        itemFacade = new ItemFacade();
        
        //mocking an EntityManager-object
        itemFacade.em = mock(EntityManager.class);
        
        /*introducing a version, where em could still be private via reflection
        em = mock(EntityManager.class);
        Field field = ItemFacade.class.getDeclaredField("em");
        field.setAccessible(true);
        field.set(itemFacade, em);
        */
        
        //mocking an EntityManagerFactory-object
        emf = mock(EntityManagerFactory.class);
        //setting up replacing getter
        doReturn(emf).when(itemFacade.em).getEntityManagerFactory();
        
        //mocking a Cache-object
        c = mock(Cache.class);
        doReturn(c).when(emf).getCache();
    }
    
    /**
     * Test of create method, of class ItemFacade.
     */
    @Test
    public void testCreate() {
        Item itemEntity = new Item();
        this.itemFacade.create(itemEntity);
        verify(c).evictAll();
        verify(itemFacade.em, times(1)).persist(itemEntity);
    }
    
    @Test
    public void testEdit(){
        Item itemEntity = new Item();
        itemEntity.setTitle("MyTitle");
        this.itemFacade.edit(itemEntity);
        verify(c).evictAll();
        verify(itemFacade.em).merge(itemEntity);
    }
    
    @Test
    public void testRemove(){
        Item itemEntity = new Item();
        this.itemFacade.remove(itemEntity);
        verify(c).evictAll();
        verify(itemFacade.em).remove(itemFacade.em.merge(itemEntity));
    }
    
    @Test
    public void testFind(){
        Item itemEntity = new Item();
        Object id = itemEntity.getId();
        this.itemFacade.find(id);
        verify(c).evictAll();
        verify(itemFacade.em).find(itemEntity.getClass(), id);
    }
    
    @Test
    public void testFindAll(){
        Item itemEntity = new Item();
        
        //some mocks needed
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        doReturn( cb ).when( itemFacade.em ).getCriteriaBuilder();
        
        CriteriaQuery cq1 = mock(CriteriaQuery.class);
        doReturn( cq1 ).when( cb ).createQuery();
        
        Root root = mock(Root.class);
        doReturn(root).when(cq1).from(itemEntity.getClass());
        
        TypedQuery query = mock(TypedQuery.class);
        doReturn(query).when(itemFacade.em).createQuery(cq1);
        
        itemFacade.findAll();
        verify(c, times(1)).evictAll();
        verify(cq1).select(cq1.from(itemEntity.getClass()));
        verify(itemFacade.em).createQuery(cq1);
        verify(query).getResultList();
    }
    
    @Test
    public void testFindRange(){
        
    }
    
    
}
