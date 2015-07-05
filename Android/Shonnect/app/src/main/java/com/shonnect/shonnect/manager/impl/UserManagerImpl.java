package com.shonnect.shonnect.manager.impl;

import com.google.gson.Gson;

import com.shonnect.shonnect.manager.UserManager;
import com.shonnect.shonnect.model.User;

import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public class UserManagerImpl implements UserManager {

    public static final String KEY_TOKEN = "key-token";
    public static final String KEY_USER = "key-user";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public UserManagerImpl(SharedPreferences sharedPreferences, Gson gson) {
        this.sharedPreferences = sharedPreferences;
        this.gson = gson;
    }

    @Override
    public void storeUser(User user, String token) {
        sharedPreferences.edit().putString(KEY_USER, gson.toJson(user))
                .putString(KEY_TOKEN, token)
                .commit();
    }

    @Override
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    @Override
    public User getCurrentUser() {
        User user = null;
        String userJson = sharedPreferences.getString(KEY_USER, null);
        if (!TextUtils.isEmpty(userJson)) {
            user = gson.fromJson(userJson, User.class);
        }
        return user;
    }

    @Override
    public boolean isAuthenticated() {
        return !TextUtils.isEmpty(getToken());
    }

    @Override
    public void logout() {
        sharedPreferences.edit().clear().commit();

    }
}
