package com.shonnect.shonnect.http.model;

import com.shonnect.shonnect.model.Conversation;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public class CreateConversationResponse {
    private Conversation data;

    public Conversation getData() {
        return data;
    }

    public void setData(Conversation data) {
        this.data = data;
    }
}
