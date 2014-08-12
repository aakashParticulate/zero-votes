package com.zero.votes.persistence.entities;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;


@Entity
public class Participant implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String email;
    private boolean hasVoted;
    
    @ManyToOne
    private Poll poll;
    
    @OneToOne
    private Token token;
    
    @ManyToMany(mappedBy="participants")
    @OrderBy("name ASC")
    @ElementCollection
    private Set<ParticipantList> participantLists;

    public boolean hasVoted() {
        return this.hasVoted;
    }

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

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Set<ParticipantList> getParticipantLists() {
        return participantLists;
    }

    public void setParticipantLists(Set<ParticipantList> participantLists) {
        this.participantLists = participantLists;
    }
    
}
