package com.shonnect.shonnect.service.impl;

import com.google.gson.Gson;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.shonnect.shonnect.dagger.component.DaggerUserComponent;
import com.shonnect.shonnect.dagger.component.UserComponent;
import com.shonnect.shonnect.dagger.module.ApplicationModule;
import com.shonnect.shonnect.dagger.module.NetworkModule;
import com.shonnect.shonnect.dagger.module.UserModule;
import com.shonnect.shonnect.http.model.JoinRoomResponse;
import com.shonnect.shonnect.manager.UserManager;
import com.shonnect.shonnect.model.ChatMessage;
import com.shonnect.shonnect.service.ChatService;
import com.shonnect.shonnect.util.Constant;

import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.net.URISyntaxException;
import java.util.Collections;

import javax.inject.Inject;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public class AndroidChatService extends Service implements ChatService {

    public static final int SOCKET_RECONNECTION_DELAY = 30000;
    private static final String TAG = "D.Vu";
    public static final String SOCKET_URL = Constant.API_ENDPOINT;
    private static final long SOCKET_TIMEOUT = 35000;
    private Socket socket;
    private ChatEventListener chatEventListener;
    @Inject
    UserManager userManager;
    @Inject
    Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();
        UserComponent userComponent = DaggerUserComponent.builder()
                .userModule(new UserModule())
                .applicationModule(new ApplicationModule(getApplicationContext()))
                .networkModule(new NetworkModule())
                .build();
        userComponent.inject(this);
        initConnectionIfNeccessary();

    }

    @Override
    public void sendChat(ChatMessage chatMessage) {
        if (socket != null) {
            socket.emit("sendChat", chatMessage.getMessage());
        }

    }

    @Override
    public void initChatWithId(String conversationId) {
        if (socket != null) {
            socket.emit("joinRoom", conversationId);
        }
    }

    @Override
    public void setOnChatEventListener(ChatEventListener chatEventListener) {
        this.chatEventListener = chatEventListener;
    }

    private void initConnectionIfNeccessary() {
        if (socket == null) {
            IO.Options socketOptions = new IO.Options();
            socketOptions.forceNew = true;
            socketOptions.reconnection = true;
            socketOptions.reconnectionDelay = SOCKET_RECONNECTION_DELAY;
            socketOptions.timeout = SOCKET_TIMEOUT;
            if (userManager.isAuthenticated()) {
                socketOptions.query = "token=" + userManager.getToken();
            }

            try {
                socket = IO.socket(SOCKET_URL, socketOptions);
                socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
                socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
                socket.on(Socket.EVENT_CONNECT, onConnectSuccess);
                socket.on("joinRoomSuccessfully", onJoinRoomSuccess);
                socket.on("updateChat", onUpdateChat);
                socket.on("log", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.d(TAG, "Log data " + args[0] + " " + args[1]);
                    }
                });

                socket.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "on connect error " + args[0]);
            if (chatEventListener != null) {
                chatEventListener.onChatDisconnected();
            }

        }
    };

    private Emitter.Listener onConnectSuccess = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "on connect success");
            if (chatEventListener != null) {
                chatEventListener.onChatConnected();
            }
        }
    };

    private Emitter.Listener onJoinRoomSuccess = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "join room " + args[0]);
            JSONObject joinRoomResponse = (JSONObject) args[0];
            if (joinRoomResponse != null) {
                JoinRoomResponse joinResponse = gson.fromJson(joinRoomResponse.toString(), JoinRoomResponse.class);
                if (chatEventListener != null) {
                    chatEventListener.onChatJoined(joinResponse.getMessages());
                }
            }
        }
    };

    private Emitter.Listener onUpdateChat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject chatJson = (JSONObject) args[0];
            Log.d(TAG, "Update chat " + chatJson);
            if (chatJson != null) {
                ChatMessage chatMessage = gson.fromJson(chatJson.toString(), ChatMessage.class);
                if (chatEventListener != null) {
                    chatEventListener.onChatMessageReceived(Collections.singletonList(chatMessage));
                }
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ChatBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            socket.off();
            socket.disconnect();
        }
    }

    public class ChatBinder extends Binder {

        public ChatBinder() {
            initConnectionIfNeccessary();
        }

        public com.shonnect.shonnect.service.ChatService getChatService() {
            return AndroidChatService.this;
        }

    }
}
