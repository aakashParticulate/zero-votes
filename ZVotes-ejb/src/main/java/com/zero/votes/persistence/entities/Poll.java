package com.zero.votes.persistence.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Poll implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String title;
    private String description;
    private PollState pollState;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    private boolean participationTracking;
    
    @ManyToMany
    @OrderBy("username ASC")
    private Set<Organizer> organizers;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.REMOVE)
    @OrderBy("title ASC")
    private Set<Item> items;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.REMOVE)
    @OrderBy("email ASC")
    private Set<Participant> participants;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.REMOVE)
    @OrderBy("id ASC")
    private Set<Token> tokens;

    public Poll() {
        this.pollState = PollState.PREPARED;
        this.organizers = new HashSet<Organizer>();
        this.items = new HashSet<Item>();
        this.participants = new HashSet<Participant>();
        this.tokens = new HashSet<Token>();
    }

    public Long getId() {
        return id;
    }

    public void setPollState(PollState pollState) {
        this.pollState = pollState;
    }

    public boolean isPollFinished() {
        switch (this.pollState) {
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

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
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
