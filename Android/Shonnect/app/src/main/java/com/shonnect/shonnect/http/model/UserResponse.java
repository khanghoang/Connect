package com.shonnect.shonnect.http.model;

import com.shonnect.shonnect.model.User;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public class UserResponse {
    private User user;
    private String token;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
