package com.zero.votes.ldap;

import java.util.Objects;

public class LdapUser implements Comparable<LdapUser> {
    private static final long serialVersionUID = -1627443224706231661L;
    private Long id;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
        
    public Long getId() {
            return id;
    }

    public void setId(Long id) {
            this.id = id;
    }

    public String getName() {
            return name;
    }

    public void setName(String name) {
            this.name = name;
    }

    public String getFirstName() {
            return firstName;
    }

    public void setFirstName(String firstName) {
            this.firstName = firstName;
    }

    public String getLastName() {
            return lastName;
    }

    public void setLastName(String lastName) {
            this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LdapUser other = (LdapUser) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public int compareTo(LdapUser ldapUser) {
        if (getClass() != ldapUser.getClass()) {
                throw new IllegalArgumentException(
                                "Can't compare " + getClass().getSimpleName() +
                                " to " + ldapUser.getClass().getSimpleName());
        }
        if (equals(ldapUser)) {
                return 0;
        }
        int r = getName().compareTo(ldapUser.getName());
        if (r == 0) {
                r = getId().compareTo(ldapUser.getId());
        }
        return r;
    }
}
