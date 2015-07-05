package com.shonnect.shonnect.http.model;

import com.google.gson.annotations.SerializedName;

import com.shonnect.shonnect.model.ChatMessage;

import java.util.List;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/5/15.
 */
public class JoinRoomResponse {
    @SerializedName("_id")
    private String id;
    private List<ChatMessage> messages;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
}
