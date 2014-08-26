package com.zero.votes.ldap;

import com.zero.votes.persistence.OrganizerFacade;
import com.zero.votes.persistence.entities.Organizer;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class LdapLogic {

    @EJB
    private OrganizerFacade organizerFacade;

    public LdapUser lookupUser(String uid) {
        return UnikoLdapLookup.lookupPerson(uid);
    }

    public LdapUser getUser(String uid) {
        return getOrganizer(uid).createLdapUser();
    }

    public Organizer getOrganizer(String uid) {
        LdapUser ldapUser = lookupUser(uid);
        if (ldapUser == null) {
            return null;
        }
        Organizer p = organizerFacade.findBy("username", uid);
        if (p == null) {
            p = new Organizer();
            p.setUsername(ldapUser.getName());
            p.setForename(ldapUser.getFirstName());
            p.setSurname(ldapUser.getLastName());
            p.setEmail(ldapUser.getEmail());
            organizerFacade.create(p);
            return p;
        } else {
            p.setUsername(ldapUser.getName());
            p.setForename(ldapUser.getFirstName());
            p.setSurname(ldapUser.getLastName());
            p.setEmail(ldapUser.getEmail());
            return organizerFacade.edit(p);
        }
    }

}
