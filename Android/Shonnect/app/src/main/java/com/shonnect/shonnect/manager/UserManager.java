package com.shonnect.shonnect.manager;

import com.shonnect.shonnect.model.User;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public interface UserManager {
    void storeUser(User user, String token);
    String getToken();
    User getCurrentUser();
    boolean isAuthenticated();

    void logout();
}
