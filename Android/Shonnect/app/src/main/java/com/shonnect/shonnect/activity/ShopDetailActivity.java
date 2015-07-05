package com.shonnect.shonnect.activity;

import com.shonnect.shonnect.R;
import com.shonnect.shonnect.dagger.component.ChatRestComponent;
import com.shonnect.shonnect.dagger.component.DaggerChatRestComponent;
import com.shonnect.shonnect.dagger.module.RestChatModule;
import com.shonnect.shonnect.dagger.module.UserModule;
import com.shonnect.shonnect.manager.UserManager;
import com.shonnect.shonnect.model.Conversation;
import com.shonnect.shonnect.model.Shop;
import com.shonnect.shonnect.service.ChatRestService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import javax.inject.Inject;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/5/15.
 */
public class ShopDetailActivity extends BaseInjectedActivity implements View.OnClickListener {

    private static final String EXTRA_SHOP_DETAIL = "extra-shop-detail";
    private WebView wvContent;
    private ImageView btConversation;
    private Shop shop;

    @Inject
    UserManager userManager;
    @Inject
    ChatRestService chatRestService;

    public static Intent getLaunchIntent(Context context, Shop shop) {
        Intent launchIntent = new Intent(context, ShopDetailActivity.class);
        launchIntent.putExtra(EXTRA_SHOP_DETAIL, shop);
        return launchIntent;
    }

    @Override
    public void onClick(View v) {
        showLoadingDialog();
        chatRestService.createNewConversation(
                shop.getId(),
                new ChatRestService.OnConversationCreatedListener() {
                    @Override
                    public void onConversationCreated(Conversation newConversation, Exception ex) {
                        hideLoadingDialog();
                        if (ex != null) {
                            Toast.makeText(getApplicationContext(), "Cannot connect to server. Please try again",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            goToChat(newConversation);
                        }
                    }
                });
    }

    private void goToChat(Conversation conversation) {
        Intent goToChatPage = ChatActivity.getLaunchIntent(this, conversation);
        startActivity(goToChatPage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        injectDependencies();
        shop = (Shop) getIntent().getSerializableExtra(EXTRA_SHOP_DETAIL);
        wvContent = (WebView) findViewById(R.id.wv_content);
        btConversation= (ImageView) findViewById(R.id.bt_chat);
        btConversation.setOnClickListener(this);
        wvContent.loadUrl(shop.getUrl());
    }

    private void injectDependencies() {
        ChatRestComponent chatRestComponent = DaggerChatRestComponent.builder()
                .userModule(new UserModule())
                .applicationModule(getApplicatioModule())
                .networkModule(getNetworkModule())
                .restChatModule(new RestChatModule())
                .build();
        chatRestComponent.inject(this);
    }
}
