package com.zero.votes.persistence.entities;

public enum ItemType {
    YES_NO("YesOrNo"),
    ONE_OF_N("OneOfN"),
    M_OF_N("MOfN");

    private final String label;

    ItemType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
