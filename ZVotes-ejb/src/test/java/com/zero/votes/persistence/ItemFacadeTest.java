/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zero.votes.persistence;

import com.zero.votes.persistence.entities.Item;
import java.util.List;
import javax.ejb.embeddable.EJBContainer;
import javax.persistence.EntityManager;
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
    
    @Before
    public void setUp() {
        itemFacade = new ItemFacade();
        itemFacade.em = mock(EntityManager.class);
    }
    
    /**
     * Test of create method, of class ItemFacade.
     */
    @Test
    public void testCreate() throws Exception {
        System.out.println("create");
        Item itemEntity = new Item();
        this.itemFacade.create(itemEntity);
        verify(itemFacade.em, times(1)).persist(itemEntity);
    }

    /**
     * Test of edit method, of class ItemFacade.
     */
    @Test
    public void testEdit() throws Exception {
        System.out.println("edit");
        Item entity = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        ItemFacade instance = (ItemFacade)container.getContext().lookup("java:global/classes/ItemFacade");
        Item expResult = null;
        Item result = instance.edit(entity);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class ItemFacade.
     */
    @Test
    public void testRemove() throws Exception {
        System.out.println("remove");
        Item entity = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        ItemFacade instance = (ItemFacade)container.getContext().lookup("java:global/classes/ItemFacade");
        instance.remove(entity);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of find method, of class ItemFacade.
     */
    @Test
    public void testFind() throws Exception {
        System.out.println("find");
        Object id = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        ItemFacade instance = (ItemFacade)container.getContext().lookup("java:global/classes/ItemFacade");
        Item expResult = null;
        Item result = instance.find(id);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAll method, of class ItemFacade.
     */
    @Test
    public void testFindAll() throws Exception {
        System.out.println("findAll");
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        ItemFacade instance = (ItemFacade)container.getContext().lookup("java:global/classes/ItemFacade");
        List<Item> expResult = null;
        List<Item> result = instance.findAll();
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findRange method, of class ItemFacade.
     */
    @Test
    public void testFindRange() throws Exception {
        System.out.println("findRange");
        int[] range = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        ItemFacade instance = (ItemFacade)container.getContext().lookup("java:global/classes/ItemFacade");
        List<Item> expResult = null;
        List<Item> result = instance.findRange(range);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of count method, of class ItemFacade.
     */
    @Test
    public void testCount() throws Exception {
        System.out.println("count");
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        ItemFacade instance = (ItemFacade)container.getContext().lookup("java:global/classes/ItemFacade");
        int expResult = 0;
        int result = instance.count();
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of countBy method, of class ItemFacade.
     */
    @Test
    public void testCountBy_String_Object() throws Exception {
        System.out.println("countBy");
        String fieldName = "";
        Object value = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        ItemFacade instance = (ItemFacade)container.getContext().lookup("java:global/classes/ItemFacade");
        int expResult = 0;
        int result = instance.countBy(fieldName, value);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of countBy method, of class ItemFacade.
     */
    @Test
    public void testCountBy_StringArr_ObjectArr() throws Exception {
        System.out.println("countBy");
        String[] fieldNames = null;
        Object[] values = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        ItemFacade instance = (ItemFacade)container.getContext().lookup("java:global/classes/ItemFacade");
        int expResult = 0;
        int result = instance.countBy(fieldNames, values);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findBy method, of class ItemFacade.
     */
    @Test
    public void testFindBy_String_Object() throws Exception {
        System.out.println("findBy");
        String fieldName = "";
        Object value = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        ItemFacade instance = (ItemFacade)container.getContext().lookup("java:global/classes/ItemFacade");
        Item expResult = null;
        Item result = instance.findBy(fieldName, value);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findBy method, of class ItemFacade.
     */
    @Test
    public void testFindBy_StringArr_ObjectArr() throws Exception {
        System.out.println("findBy");
        String[] fieldNames = null;
        Object[] values = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        ItemFacade instance = (ItemFacade)container.getContext().lookup("java:global/classes/ItemFacade");
        Item expResult = null;
        Item result = instance.findBy(fieldNames, values);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAllBy method, of class ItemFacade.
     */
    @Test
    public void testFindAllBy_String_Object() throws Exception {
        System.out.println("findAllBy");
        String fieldName = "";
        Object value = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        ItemFacade instance = (ItemFacade)container.getContext().lookup("java:global/classes/ItemFacade");
        List<Item> expResult = null;
        List<Item> result = instance.findAllBy(fieldName, value);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAllBy method, of class ItemFacade.
     */
    @Test
    public void testFindAllBy_StringArr_ObjectArr() throws Exception {
        System.out.println("findAllBy");
        String[] fieldNames = null;
        Object[] values = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        ItemFacade instance = (ItemFacade)container.getContext().lookup("java:global/classes/ItemFacade");
        List<Item> expResult = null;
        List<Item> result = instance.findAllBy(fieldNames, values);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findRangeBy method, of class ItemFacade.
     */
    @Test
    public void testFindRangeBy_3args_1() throws Exception {
        System.out.println("findRangeBy");
        String fieldName = "";
        Object value = null;
        int[] range = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        ItemFacade instance = (ItemFacade)container.getContext().lookup("java:global/classes/ItemFacade");
        List<Item> expResult = null;
        List<Item> result = instance.findRangeBy(fieldName, value, range);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findRangeBy method, of class ItemFacade.
     */
    @Test
    public void testFindRangeBy_3args_2() throws Exception {
        System.out.println("findRangeBy");
        String[] fieldNames = null;
        Object[] values = null;
        int[] range = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        ItemFacade instance = (ItemFacade)container.getContext().lookup("java:global/classes/ItemFacade");
        List<Item> expResult = null;
        List<Item> result = instance.findRangeBy(fieldNames, values, range);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
