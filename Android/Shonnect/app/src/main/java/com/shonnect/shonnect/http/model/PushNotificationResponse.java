package com.shonnect.shonnect.http.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/5/15.
 */
public class PushNotificationResponse {
    @SerializedName("alert")
    private String content;
    @SerializedName("conversation_id")
    private String conversationId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
