package com.shonnect.shonnect.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public class User implements Serializable {
    @SerializedName("_id")
    private String id;
    private String accessToken;
    private String name;
    private String avatar;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof User) {
            User comparedUser = (User) o;
            return comparedUser.getId().equals(id);
        }
        return super.equals(o);
    }
}
