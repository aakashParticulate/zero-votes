package com.zero.votes.cronjobs;

import com.zero.votes.persistence.entities.Participant;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.web.util.EMailer;
import java.util.Locale;

public class ReminderMailTask implements Runnable {
    
    private Poll poll;
    private EMailer eMailer;
    private Locale locale;

    public ReminderMailTask(Poll poll, EMailer eMailer, Locale locale) {
        this.poll = poll;
        this.eMailer = eMailer;
        this.locale = locale;
    }
    
    @Override
    public void run() {
        for(Participant participant: poll.getParticipants()) {
            eMailer.sendReminderMail(poll, participant, locale);
        }
    }

}
