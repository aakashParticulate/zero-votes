/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zero.votes.ldap;

import com.zero.votes.persistence.ItemFacade;
import com.zero.votes.persistence.OrganizerFacade;
import com.zero.votes.persistence.entities.Organizer;
import java.lang.reflect.Field;
import javax.ejb.embeddable.EJBContainer;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Only works, when logged in to University's VPN
 */
public class LdapLogicTest {    
    
    private LdapLogic logic;
    
    @Before
    public void setUp(){
        logic = new LdapLogic();
    }
    
    /**
     * Test of lookupUser method, of class LdapLogic.
     * 
     */
    @Test
    public void testLookupUser() {
        LdapUser user = logic.lookupUser("heinz");
        assertEquals("Marcel",user.getFirstName());
        assertEquals("Heinz", user.getLastName());
        assertEquals("heinz@uni-koblenz.de",user.getEmail());
        assertEquals("heinz", user.getName());
    }

    /**
     * Test of getUser method, of class LdapLogic.
     */
    @Test
    public void testGetOrganizerInvalid() throws Exception {
        assertNull(logic.getOrganizer("hein"));
    }
    
    @Test
    public void testGetOrganizerNew() throws Exception {
        OrganizerFacade of = mock(OrganizerFacade.class);
        Field field = LdapLogic.class.getDeclaredField("organizerFacade");
        field.setAccessible(true);
        field.set(logic, of);
        Organizer org = null;
        doReturn(org).when(of).findBy("username", "heinz");
        logic.getOrganizer("heinz");
        verify(of).create(any(Organizer.class));
    }

    /**
     * Test of getOrganizer method, of class LdapLogic.
     */
    @Test
    public void testGetOrganizerExisting() throws Exception {
        OrganizerFacade of = mock(OrganizerFacade.class);
        Field field = LdapLogic.class.getDeclaredField("organizerFacade");
        field.setAccessible(true);
        field.set(logic, of);
        Organizer org = new Organizer();
        doReturn(org).when(of).findBy("username", "heinz");
        logic.getOrganizer("heinz");
        verify(of).edit(any(Organizer.class));
    }
    
    @Test
    public void testGetUser() throws Exception {
        OrganizerFacade of = mock(OrganizerFacade.class);
        Field field = LdapLogic.class.getDeclaredField("organizerFacade");
        field.setAccessible(true);
        field.set(logic, of);
        Organizer org1 = mock(Organizer.class);
        doReturn(org1).when(of).findBy("username", "heinz");
        Organizer org2 = mock(Organizer.class);
        doReturn(org2).when(of).edit(any(Organizer.class));
        logic.getUser("heinz");
        verify(org2).createLdapUser();
    }
    

    @Test
    public void testGetUserInvalid(){
        assertNull(logic.getUser("hein"));
    }
    
}
