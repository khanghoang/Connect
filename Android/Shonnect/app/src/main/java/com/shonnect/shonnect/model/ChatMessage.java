package com.shonnect.shonnect.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public class ChatMessage implements Serializable {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
    @SerializedName("_id")
    private String id;
    private User user;
    private String content;
    private Date createdAt;
    private Date updatedAt;

    public long getTimestamp() {
        return createdAt.getTime();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReadableTime() {
        return sdf.format(createdAt);
    }

    public String getMessage() {
        return content;
    }

    public void setMessage(String message) {
        this.content = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof ChatMessage) {
            ChatMessage comparedChatMsg = (ChatMessage) o;
            return id.equalsIgnoreCase(comparedChatMsg.getId());
        }
        return super.equals(o);
    }
}
