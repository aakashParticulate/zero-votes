package com.zero.votes.cronjobs;

import com.zero.votes.persistence.entities.Participant;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.web.util.EMailer;
import java.util.Locale;

/**
 * This task offers the functionality to send an information mail,
 * that a poll has been started.
 */
public class StartedMailTask implements Runnable {
    
    private Poll poll;
    private EMailer eMailer;
    private Locale locale;
    private String url;

    public StartedMailTask(Poll poll, EMailer eMailer, Locale locale, String url) {
        this.poll = poll;
        this.eMailer = eMailer;
        this.locale = locale;
        this.url = url;
    }
    
    @Override
    public void run() {
        int i = 0;
        for(Participant participant: poll.getParticipants()) {
            String token;
            if (poll.isParticipationTracking()) {
                token = participant.getToken().getTokenString();
            } else {
                token = poll.getTokens().get(i).getTokenString();
                i++;
            }
            eMailer.sendStartedMail(poll, participant, locale, url, token);
        }
    }

}
