package com.ciao.app.datamodel;

/**
 * Created by rajat on 30/1/15.
 */
public class ChatMessage {
    public boolean left;
    public String message;

    public ChatMessage(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
    }
}