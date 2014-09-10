package com.zero.votes.persistence.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private ItemType type;
    private int m;
    private boolean ownOptions;

    @ManyToOne
    private Poll poll;

    @OneToMany(mappedBy = "item", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @OrderBy
    private List<Vote> votes;

    @OneToMany(mappedBy = "item", fetch = FetchType.EAGER, cascade=CascadeType.REMOVE)
    @OrderBy
    private List<ItemOption> options;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public List<ItemOption> getOptions() {
        return options;
    }

    public void setOptions(List<ItemOption> options) {
        this.options = options;
    }

    public boolean isOwnOptions() {
        return ownOptions;
    }

    public void setOwnOptions(boolean ownOptions) {
        this.ownOptions = ownOptions;
    }

    @Override
    public String toString() {
        return "Item{" + "id=" + id + ", title=" + title + '}';
    }

}
