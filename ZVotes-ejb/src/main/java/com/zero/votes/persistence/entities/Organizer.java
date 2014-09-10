package com.zero.votes.persistence.entities;

import com.zero.votes.ldap.LdapUser;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class Organizer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;
    private String forename;
    private String surname;

    @Column(unique = true)
    private String email;
    private String encryptedPassword;
    private boolean admin;

    @ManyToMany(mappedBy = "organizers", fetch = FetchType.EAGER)
    @OrderBy("title ASC")
    private List<Poll> polls;

    @OneToMany(mappedBy = "organizer", fetch = FetchType.EAGER)
    private List<RecipientList> recipientLists;

    public Organizer() {
        polls = new ArrayList<>();
        recipientLists = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public List<Poll> getPolls() {
        return polls;
    }

    public void setPolls(List<Poll> polls) {
        this.polls = polls;
    }

    public List<RecipientList> getRecipientLists() {
        return recipientLists;
    }

    public void setRecipientLists(List<RecipientList> recipientLists) {
        this.recipientLists = recipientLists;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public LdapUser createLdapUser() {
        LdapUser to = new LdapUser();
        to.setId(getId());
        to.setName(getUsername());
        to.setFirstName(getForename());
        to.setLastName(getSurname());
        to.setEmail(getEmail());
        return to;
    }

    @Override
    public String toString() {
        return "Organizer{" + "id=" + id + ", username=" + username + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Organizer other = (Organizer) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    

}
