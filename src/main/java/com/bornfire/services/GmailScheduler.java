package com.bornfire.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GmailScheduler {

    @Autowired
    private GmailReadService service;

//    @Scheduled(fixedDelay = 1000) // every 1 second
    public void fetchEmails() throws Exception {
        System.out.println("------ Main Receiver ------------ ");
        service.readAndSaveEmails();
    }
}
