package com.bornfire.services;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bornfire.entity.GmailInbox;
import com.bornfire.entity.GmailInboxRepository;

import javax.mail.Flags;
import javax.mail.search.FlagTerm;
import javax.mail.BodyPart;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.Flags;

@Service
public class GmailReadService {

    @Value("${gmail.username}")
    private String username;

    @Value("${gmail.app.password}")
    private String password;

    @Autowired
    private GmailInboxRepository repo;

    public List<Map> readAndSaveEmails() throws Exception {

        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.ssl.enable", "true");
        props.put("mail.imaps.ssl.protocols", "TLSv1.2");
        props.put("mail.imaps.connectiontimeout", "10000");
        props.put("mail.imaps.timeout", "10000");
        props.put("mail.imaps.ssl.trust", "imap.gmail.com");
        props.put("mail.imaps.ssl.checkserveridentity", "false");
        props.put("mail.imaps.peek", "true");


        Session session = Session.getInstance(props);
        Store store = session.getStore("imaps");
        store.connect(username, password);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        SearchTerm unread = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
        SearchTerm from = new FromStringTerm("rsanthoshrst@gmail.com");

        Message[] messages = inbox.search(new AndTerm(unread, from));
        System.out.println("✅ Total unread messages found: " + messages.length);
        System.out.println("---------- Message Save -------");
        List<Map> mail = new ArrayList<>();
        for (Message msg : messages) {

        	System.out.println(msg.getFrom()[0].toString() +"   --------------------");
        	System.out.println(msg.getSubject() +"   --------------------");
        	System.out.println(extractText(msg) +"   --------------------");
        	System.out.println(msg.getReceivedDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime() +"   --------------------");
        	
        	Map<String,Object> mapT = new HashMap<>();
        	
        	mapT.put("from",msg.getFrom()[0].toString());
        	mapT.put("subject",msg.getSubject());
        	mapT.put("body",extractText(msg));
        	mapT.put("recivedtime",msg.getReceivedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        	
        	mail.add(mapT);
        	
//            mail.setFromEmail(msg.getFrom()[0].toString());
//            mail.setSubject(msg.getSubject());
//            mail.setBody(extractText(msg));
//            mail.setReceivedAt(
//                msg.getReceivedDate()
//                   .toInstant()
//                   .atZone(ZoneId.systemDefault())
//                   .toLocalDateTime()
//            );

//            repo.save(mail);

            // mark mail as read
//            msg.setFlag(Flags.Flag.SEEN, true);
        }

        inbox.close(false);
        store.close();
        
        return mail;
    }

    private String extractText(Message message) throws Exception {

        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        }

        if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();

            for (int i = 0; i < multipart.getCount(); i++) {
            	BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    return bodyPart.getContent().toString();
                }
            }
        }
        return "";
    }
}
