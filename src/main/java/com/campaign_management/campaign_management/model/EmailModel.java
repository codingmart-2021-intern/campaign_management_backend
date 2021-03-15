package com.campaign_management.campaign_management.model;

public class EmailModel {
    public String toAddress;
    public String senderName;
    public String content;
    public String subject;

    public String getContent() {
        return content;
    }
    public String getSenderName() {
        return senderName;
    }
    public String getSubject() {
        return subject;
    }
    public String getToAddress() {
        return toAddress;
    }
}
