package com.shonnect.shonnect.service;

import com.shonnect.shonnect.model.ChatMessage;

import java.util.List;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public interface ChatService {
    void sendChat(ChatMessage chatMessage);
    void initChatWithId(String toId);

    void setOnChatEventListener(ChatEventListener chatEventListener);

    interface ChatEventListener {
        void onChatJoined(List<ChatMessage> messages);
        void onChatMessageReceived(List<ChatMessage> chatMessageList);
        void onChatConnected();
        void onChatDisconnected();
    }
}
