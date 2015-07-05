package com.shonnect.shonnect.service;

import com.shonnect.shonnect.http.model.UserResponse;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public interface UserService {

    void loginWithFacebook(String fbAccessToken, OnUserLoggedInListener onUserLoggedInListener);

    interface OnUserLoggedInListener {
        void onLoginFinished(UserResponse user, Exception ex);
    }
}
