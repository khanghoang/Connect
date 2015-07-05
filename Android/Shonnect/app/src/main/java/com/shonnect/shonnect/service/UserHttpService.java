package com.shonnect.shonnect.service;

import com.shonnect.shonnect.http.model.UserResponse;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public interface UserHttpService {
    @POST("/login/facebookLogin")
    @FormUrlEncoded
    void login(@Field("access_token" )String accessToken, Callback<UserResponse> callback);

}
