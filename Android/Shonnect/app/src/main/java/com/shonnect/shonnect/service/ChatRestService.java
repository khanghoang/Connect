package com.shonnect.shonnect.service;

import com.shonnect.shonnect.model.Conversation;

import java.util.List;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public interface ChatRestService {
    void getConversationList(OnConversationListRetrievedListener onConversationListRetrievedListener);
    void createNewConversation(String toUserId, OnConversationCreatedListener onConversationCreatedListener);
    void followUser(String userId, OnFollowListener onFollowListener);
    void unFollowUser(String userId, OnFollowListener onFollowListener);

    interface OnConversationListRetrievedListener {
        void onConversationListRetrieved(List<Conversation> conversationList, Exception ex);
    }
    interface OnConversationCreatedListener {
        void onConversationCreated(Conversation newConversation, Exception ex);
    }

    interface OnFollowListener {
        void onFollowedUser(boolean isSuccess);
    }
}
