package com.shonnect.shonnect.dagger.module;

import com.shonnect.shonnect.service.UserHttpService;
import com.shonnect.shonnect.service.UserService;
import com.shonnect.shonnect.service.impl.UserServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
@Module
public class UserModule {
    @Provides
    @Singleton
    public UserService providesUserService(UserHttpService userHttpService) {
        return new UserServiceImpl(userHttpService);
    }

}
