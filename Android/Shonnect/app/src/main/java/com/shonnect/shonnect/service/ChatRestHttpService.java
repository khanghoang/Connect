package com.shonnect.shonnect.service;

import com.shonnect.shonnect.http.model.ConversationListResponse;
import com.shonnect.shonnect.http.model.CreateConversationResponse;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public interface ChatRestHttpService {
    @POST("/api/conversation/create")
    @FormUrlEncoded
    void createConversation(@Field("target_user_id") String toId, Callback<CreateConversationResponse> callback);
    @GET("/api/conversation/list")
    void getConversationList(Callback<ConversationListResponse> callback);

    @POST("/api/user/follow")
    @FormUrlEncoded
    void followUser(@Field("user_id") String userId, Callback<Object> callback);
    @POST("/api/user/unfollow")
    @FormUrlEncoded
    void unFollowUuer(@Field("user_id") String userId, Callback<Object> callback);


}
