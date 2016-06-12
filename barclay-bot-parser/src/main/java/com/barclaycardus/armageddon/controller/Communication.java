package com.barclaycardus.armageddon.controller;

/**
 * Created by Junaid on 12-Jun-16.
 */
public class Communication {

    String action;
    String content;
    String reply;
    Communication previousCommunication;

    public Communication(String action, String content) {
        this.action = action;
        this.content = content;
    }

    public Communication getPreviousCommunication() {
        return previousCommunication;
    }

    public void setPreviousCommunication(Communication previousCommunication) {
        this.previousCommunication = previousCommunication;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
