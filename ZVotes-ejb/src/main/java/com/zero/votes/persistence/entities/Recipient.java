package com.zero.votes.persistence.entities;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"email", "organizer"})})
public class Recipient implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name="email")
    private String email;
          
    @JoinColumn(name="organizer")
    @ManyToOne
    private Organizer organizer;
    
    @ManyToMany(mappedBy="recipients")
    @ElementCollection
    @OrderBy("name ASC")
    private Set<RecipientList> recipientLists;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    public Set<RecipientList> getRecipientLists() {
        return recipientLists;
    }

    public void setRecipientLists(Set<RecipientList> recipientLists) {
        this.recipientLists = recipientLists;
    }
    
    
}
