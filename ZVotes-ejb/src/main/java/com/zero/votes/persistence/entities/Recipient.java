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
    private Set<RecipientList> recipientLists;
    
    
}
