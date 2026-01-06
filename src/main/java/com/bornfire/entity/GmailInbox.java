package com.bornfire.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "gmail_inbox")
public class GmailInbox {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gmail_seq")
	@SequenceGenerator(
	    name = "gmail_seq",
	    sequenceName = "GMAIL_INBOX_SEQ",
	    allocationSize = 1
	)
    private Long id;

    @Column(name = "from_email")
    private String fromEmail;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "RECEIVED_AT")
    private LocalDateTime receivedAt;

    // ===== GETTERS & SETTERS =====

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }
}
