/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zero.votes.web.util;

import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.persistence.entities.Token;
import java.util.Date;
import java.util.ResourceBundle;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Marcel
 */
@Stateless
public class EMailer {

    // Mail delivery
    @Resource(lookup = "mail/uniko-mail")
    private Session mailSession;

    public boolean sendPublishMail(Poll poll, Token token, String recipientmail) {
        try {
            Message msg = new MimeMessage(mailSession);
            msg.setSubject(poll.getTitle()+" : published");
            msg.setSentDate(new Date());
            msg.setReplyTo(InternetAddress.parse("heinz@uni-koblenz.de", false));
            
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientmail, false));
            
            FacesContext context = FacesContext.getCurrentInstance();
            ResourceBundle bundle = ResourceBundle.getBundle("com.zero.votes.Locale", context.getViewRoot().getLocale());
            String text = bundle.getString("PublishMail");
            //title, start, end, number, URL, token
            text = text.replace("$title$", poll.getTitle());
            text = text.replace("$start$", poll.getStartDate().toString());
            text = text.replace("$end$", poll.getEndDate().toString());
            text = text.replace("$number$", Integer.toString(poll.getParticipants().size()));
            text = text.replace("$URL$", "http://www.zerovotes.de");
            text = text.replace("$token$", token.getTokenString());
            msg.setText(text);
            Transport.send(msg);
        } catch (MessagingException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
