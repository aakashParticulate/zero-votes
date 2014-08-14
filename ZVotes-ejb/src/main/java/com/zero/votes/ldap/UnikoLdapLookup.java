package com.zero.votes.ldap;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class UnikoLdapLookup {

    public static LdapUser lookupPerson(String uid) {
        LdapUser person = new LdapUser();
        person.setName(uid);

        Hashtable<String, String> env = new Hashtable<>();

        String sp = "com.sun.jndi.ldap.LdapCtxFactory";
        env.put(Context.INITIAL_CONTEXT_FACTORY, sp);

        String ldapUrl = "ldaps://ldap.uni-koblenz.de";
        env.put(Context.PROVIDER_URL, ldapUrl);

        DirContext dctx = null;
        try {
            dctx = new InitialDirContext(env);
            String base = "ou=people,ou=koblenz,dc=uni-koblenz-landau,dc=de";

            SearchControls sc = new SearchControls();
            String[] attributeFilter = {"uid", "sn", "givenname", "mail"};
            sc.setReturningAttributes(attributeFilter);
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String filter = "(&(objectClass=Person)(uid=" + uid + "))";
            NamingEnumeration results = dctx.search(base, filter, sc);
            if (results.hasMore()) {
                SearchResult sr = (SearchResult) results.next();
                Attributes attrs = sr.getAttributes();
                Attribute a = attrs.get("uid");
                if (a != null) {
                    person.setName((String) a.get());
                }
                a = attrs.get("sn");
                if (a != null) {
                    person.setLastName((String) a.get());
                }
                a = attrs.get("givenname");
                if (a != null) {
                    person.setFirstName((String) a.get());
                }
                a = attrs.get("mail");
                if (a != null) {
                    person.setEmail((String) a.get());
                }
            } else {
                person = null;
            }
        } catch (NamingException ex) {
            Logger.getLogger(UnikoLdapLookup.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (dctx != null) {
                try {
                    dctx.close();
                } catch (NamingException ex) {
                    Logger.getLogger(UnikoLdapLookup.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return person;
    }
}
