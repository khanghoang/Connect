package com.shonnect.shonnect.dagger.component;

import com.shonnect.shonnect.activity.ChatActivity;
import com.shonnect.shonnect.activity.ShopDetailActivity;
import com.shonnect.shonnect.dagger.module.ApplicationModule;
import com.shonnect.shonnect.dagger.module.NetworkModule;
import com.shonnect.shonnect.dagger.module.RestChatModule;
import com.shonnect.shonnect.dagger.module.UserModule;
import com.shonnect.shonnect.fragment.ConversationListFragment;
import com.shonnect.shonnect.fragment.ShopListFragment;
import com.shonnect.shonnect.manager.UserManager;
import com.shonnect.shonnect.service.ChatRestHttpService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
@Component(modules = {UserModule.class, ApplicationModule.class, RestChatModule.class, NetworkModule.class})
@Singleton
public interface ChatRestComponent {
    void inject(ConversationListFragment conversationListFragment);
    void inject(ChatActivity chatActivity);
    UserManager providesUserManager();
    ChatRestHttpService providesChatRestHttpService();

    void inject(ShopListFragment fragment);

    void inject(ShopDetailActivity shopDetailActivity);
}
