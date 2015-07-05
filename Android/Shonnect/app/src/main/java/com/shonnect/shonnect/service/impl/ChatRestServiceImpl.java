package com.shonnect.shonnect.service.impl;

import com.shonnect.shonnect.http.model.ConversationListResponse;
import com.shonnect.shonnect.http.model.CreateConversationResponse;
import com.shonnect.shonnect.service.ChatRestHttpService;
import com.shonnect.shonnect.service.ChatRestService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public class ChatRestServiceImpl implements ChatRestService {
    private ChatRestHttpService chatRestHttpService;

    public ChatRestServiceImpl(ChatRestHttpService chatRestHttpService) {
        this.chatRestHttpService = chatRestHttpService;
    }

    @Override
    public void getConversationList(final OnConversationListRetrievedListener onConversationListRetrievedListener) {
        chatRestHttpService.getConversationList(new Callback<ConversationListResponse>() {
            @Override
            public void success(ConversationListResponse conversationListResponse, Response response) {
                if (onConversationListRetrievedListener != null) {
                    onConversationListRetrievedListener.onConversationListRetrieved(conversationListResponse.getData(), null);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (onConversationListRetrievedListener != null) {
                    onConversationListRetrievedListener.onConversationListRetrieved(null, error);
                }
            }
        });
    }

    @Override
    public void createNewConversation(String toUserId,
            final OnConversationCreatedListener onConversationCreatedListener) {
        chatRestHttpService.createConversation(toUserId, new Callback<CreateConversationResponse>() {
            @Override
            public void success(CreateConversationResponse createConversationResponse, Response response) {
                if (onConversationCreatedListener != null) {
                    onConversationCreatedListener.onConversationCreated(createConversationResponse.getData(), null);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (onConversationCreatedListener != null) {
                    onConversationCreatedListener.onConversationCreated(null, error);
                }
            }
        });
    }

    @Override
    public void followUser(String userId, final OnFollowListener onFollowListener) {
        chatRestHttpService.followUser(userId, new Callback<Object>() {
            @Override
            public void success(Object s, Response response) {
                if (onFollowListener != null) {
                    onFollowListener.onFollowedUser(true);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (onFollowListener != null) {
                    onFollowListener.onFollowedUser(false);
                }
            }
        });
    }

    @Override
    public void unFollowUser(String userId, final OnFollowListener onFollowListener) {
        chatRestHttpService.unFollowUuer(userId, new Callback<Object>() {
            @Override
            public void success(Object s, Response response) {
                if (onFollowListener != null) {
                    onFollowListener.onFollowedUser(true);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (onFollowListener != null) {
                    onFollowListener.onFollowedUser(false);
                }
            }
        });
    }
}
