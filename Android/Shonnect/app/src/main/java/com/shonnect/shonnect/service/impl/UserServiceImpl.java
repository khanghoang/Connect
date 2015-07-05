package com.shonnect.shonnect.service.impl;

import com.google.gson.Gson;

import com.shonnect.shonnect.http.model.UserResponse;
import com.shonnect.shonnect.service.UserHttpService;
import com.shonnect.shonnect.service.UserService;

import android.content.SharedPreferences;
import android.text.TextUtils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public class UserServiceImpl implements UserService {

    private UserHttpService userHttpService;

    public UserServiceImpl(UserHttpService userHttpService) {
        this.userHttpService = userHttpService;
    }

    @Override
    public void loginWithFacebook(String fbAccessToken, final OnUserLoggedInListener onUserLoggedInListener) {
        if (!TextUtils.isEmpty(fbAccessToken)) {
            userHttpService.login(fbAccessToken, new Callback<UserResponse>() {
                @Override
                public void success(UserResponse user, Response response) {
                    if (onUserLoggedInListener != null) {
                        onUserLoggedInListener.onLoginFinished(user, null);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (onUserLoggedInListener != null) {
                        onUserLoggedInListener.onLoginFinished(null, error);
                    }
                }
            });
        } else {
            onUserLoggedInListener.onLoginFinished(null, new IllegalArgumentException("FB access token must be provided"));
        }
    }



}
