package com.shonnect.shonnect.receiver;

import com.google.gson.Gson;

import com.parse.ParsePushBroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/5/15.
 */
public class PushNotificationReceiver extends ParsePushBroadcastReceiver {
    private Gson gson;
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
        String message = intent.getStringExtra("com.parse.Data"); // get data
        Log.d("D.Vu", "Push receive " + message);

    }
}
