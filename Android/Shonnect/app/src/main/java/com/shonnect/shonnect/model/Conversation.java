package com.shonnect.shonnect.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public class Conversation implements Serializable {

    @SerializedName("createUser")
    private User fromUser;
    @SerializedName("targetUser")
    private User toUser;
    @SerializedName("lastMessage")
    private ChatMessage messages;
    @SerializedName("messages")
    private List<ChatMessage> chatMessages;
    @SerializedName("_id")
    private String id;
    @SerializedName("isFollowed")
    private boolean isFollowed;
    @SerializedName("createdAt")
    private Date createdAt;
    @SerializedName("updatedAt")
    private Date updatedAt;


    public User getRealTargetUser(String currentUserId) {
        User targetUser = null;
        if (toUser != null && currentUserId != null && currentUserId.equals(toUser.getId())) {
            if (fromUser != null) {
                targetUser = fromUser;
            }
        }
        if (fromUser != null && currentUserId != null && currentUserId.equals(fromUser.getId())) {
            if (toUser != null) {
                targetUser = toUser;
            }
        }
        return targetUser;
    }

    public User getFromUser() {
        return fromUser;
    }

    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public ChatMessage getMessages() {
        return messages;
    }

    public void setMessages(ChatMessage messages) {
        this.messages = messages;
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

    public void setChatMessages(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public void setIsFollowed(boolean isFollowed) {
        this.isFollowed = isFollowed;
    }
}
