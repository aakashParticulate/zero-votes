package com.zero.votes.web.util;

import com.zero.votes.persistence.entities.Participant;
import com.zero.votes.persistence.entities.Poll;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Stateless
public class EMailer {

    // Mail delivery
    @Resource(lookup = "mail/uniko-mail")
    private Session mailSession;

    public boolean sendStartedMail(Poll poll, Participant participant, Locale locale, String url, String token) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("com.zero.votes.Locale", locale);
            
            Message msg = new MimeMessage(mailSession);
            msg.setSubject(poll.getTitle());
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(participant.getEmail(), false));
            
            String text = bundle.getString("StartedMail");
            //title, start, end, number, URL, token
            text = text.replace("$title$", poll.getTitle());
            text = text.replace("$start$", poll.getStartDate().toString());
            text = text.replace("$end$", poll.getEndDate().toString());
            text = text.replace("$number$", Integer.toString(poll.getParticipants().size()));
            text = text.replace("$URL$", url);
            text = text.replace("$token$", token);
            msg.setText(text);
            Transport.send(msg);
        } catch (MessagingException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean sendReminderMail(Poll poll, Participant participant, Locale locale, String url){
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("com.zero.votes.Locale", locale);
            
            Message msg = new MimeMessage(mailSession);
            msg.setSubject(bundle.getString("Reminder")+": "+poll.getTitle());
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(participant.getEmail(), false));
            
            String text = bundle.getString("ReminderMail");
            //title, start, end, number, URL, token
            text = text.replace("$title$", poll.getTitle());
            text = text.replace("$end$", poll.getEndDate().toString());
            text = text.replace("$URL$", url);
            msg.setText(text);
            Transport.send(msg);
        } catch (MessagingException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
