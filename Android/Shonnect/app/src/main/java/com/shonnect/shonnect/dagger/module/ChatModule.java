package com.shonnect.shonnect.dagger.module;

import com.shonnect.shonnect.service.ChatRestHttpService;
import com.shonnect.shonnect.service.ChatRestService;
import com.shonnect.shonnect.service.impl.ChatRestServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
@Module
public class ChatModule {
    @Provides
    @Singleton
    public ChatRestService providesChatRestService(ChatRestHttpService chatRestHttpService) {
        return new ChatRestServiceImpl(chatRestHttpService);
    }
}
