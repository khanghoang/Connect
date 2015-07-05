package com.shonnect.shonnect.http.model;

import com.shonnect.shonnect.model.Conversation;

import java.util.List;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public class ConversationListResponse {
    private List<Conversation> data;

    public List<Conversation> getData() {
        return data;
    }

    public void setData(List<Conversation> data) {
        this.data = data;
    }
}
