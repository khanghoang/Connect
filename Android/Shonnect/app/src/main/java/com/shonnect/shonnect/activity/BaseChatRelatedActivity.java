package com.shonnect.shonnect.activity;

import com.shonnect.shonnect.service.ChatService;
import com.shonnect.shonnect.service.impl.AndroidChatService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public class BaseChatRelatedActivity extends BaseInjectedActivity implements ServiceConnection {

    private static final String TAG = "D.Vu";
    private boolean isBounded = false;
    protected ChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent connectToChatServiceIntent = new Intent(this, AndroidChatService.class);
        bindService(connectToChatServiceIntent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBounded) {
            unbindService(this);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "onServiceConnected ");
        isBounded = true;
        chatService = ((AndroidChatService.ChatBinder) service).getChatService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceConnected ");
        isBounded = false;
    }


}
