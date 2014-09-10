package com.zero.votes.ldap;

import com.zero.votes.persistence.OrganizerFacade;
import com.zero.votes.persistence.entities.Organizer;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class LdapLogic {

    @EJB
    private OrganizerFacade organizerFacade;

    /**
     * Creates a LdapUser-object filled with data from a lookup in uniko's
     * ldap and returns it.
     * @param uid : the id to find a user in uniko's ldap
     * @return : created LdapUser-object
     */
    public LdapUser lookupUser(String uid) {
        return UnikoLdapLookup.lookupPerson(uid);
    }

    /**
     * Returns the LdapUser-object belonging to the organizer with id uid.
     * @param uid
     * @return 
     */
    public LdapUser getUser(String uid) {
        try {
            return getOrganizer(uid).createLdapUser();
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Searches for an existing ldapUser with id uid. If such a user exists,
     * it returns it as an organizer-object.
     * Returns an organizer-object with id uid.
     * @param uid
     * @return 
     */
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
            if (organizerFacade.countBy("admin", true) == 0) {
                p.setAdmin(true);
            }
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
