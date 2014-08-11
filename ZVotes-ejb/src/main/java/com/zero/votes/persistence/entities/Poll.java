/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zero.votes.persistence.entities;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author iekadou
 */
@Entity
public class Poll implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(unique=true)
    private String title;
    private String description;
    private PollState pollState;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar startDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar endDate;
    private boolean participationTracking;
    
    @ManyToMany(mappedBy="polls")
    @OrderBy("username ASC")
    @ElementCollection
    private Set<Organizer> organizers;
    @OneToMany(mappedBy="poll", cascade=CascadeType.REMOVE)
    @OrderBy("title ASC")
    @ElementCollection
    private Set<Item> items;
    
    @OneToMany(mappedBy="poll", cascade=CascadeType.REMOVE)
    @OrderBy("email ASC")
    @ElementCollection
    private Set<Participant> participants;
    
    @OneToMany(mappedBy="poll", cascade=CascadeType.REMOVE)
    @OrderBy("id ASC")
    @ElementCollection
    private Set<Token> tokens;

    public Long getId() {
            return id;
    }
    
    public void setPollState(PollState pollState) {
        this.pollState = pollState;
    }
    
    public boolean isPollFinished() {
        switch(this.pollState) {
            case FINISHED: 
                return true;
            case VOTING: 
                boolean result = true;
                for (Participant p : this.participants) {
                    if (p.hasVoted()) {
                        result = false;
                    }
                }
                if (result) {
                    this.setPollState(PollState.FINISHED);
                }
                return result;
            default:
                return false;
        }
    }

    public void setEndDate(Calendar endDate) {
        if (!this.isPollFinished()) {
            this.endDate = endDate;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public boolean isParticipationTracking() {
        return participationTracking;
    }

    public void setParticipationTracking(boolean participationTracking) {
        this.participationTracking = participationTracking;
    }

    public Set<Organizer> getOrganizers() {
        return organizers;
    }

    public void setOrganizers(Set<Organizer> organizers) {
        this.organizers = organizers;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public Set<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
    }

    public Set<Token> getTokens() {
        return tokens;
    }

    public void setTokens(Set<Token> tokens) {
        this.tokens = tokens;
    }
    
    
}
