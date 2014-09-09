package com.zero.votes.persistence.entities;

public enum PollState {
    PREPARING("Preparing"),
    PUBLISHED("Published"),
    STARTED("Started"),
    VOTING("Voting"),
    FINISHED("Finished");

    private final String label;

    PollState(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
