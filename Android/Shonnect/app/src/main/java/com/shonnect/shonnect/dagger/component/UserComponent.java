package com.shonnect.shonnect.dagger.component;

import com.shonnect.shonnect.activity.LoginActivity;
import com.shonnect.shonnect.dagger.module.ApplicationModule;
import com.shonnect.shonnect.dagger.module.NetworkModule;
import com.shonnect.shonnect.dagger.module.UserModule;
import com.shonnect.shonnect.manager.UserManager;
import com.shonnect.shonnect.service.UserService;
import com.shonnect.shonnect.service.impl.AndroidChatService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
@Component(modules = {ApplicationModule.class, NetworkModule.class, UserModule.class})
@Singleton
public interface UserComponent {
    void inject(LoginActivity loginActivity);
    void inject(AndroidChatService androidChatService);

    UserService getUserService();
    UserManager getUserManager();

}
