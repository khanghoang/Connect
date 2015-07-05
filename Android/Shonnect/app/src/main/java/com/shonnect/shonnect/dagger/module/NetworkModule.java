package com.shonnect.shonnect.dagger.module;

import com.google.gson.Gson;

import com.shonnect.shonnect.manager.UserManager;
import com.shonnect.shonnect.service.ChatRestHttpService;
import com.shonnect.shonnect.service.UserHttpService;
import com.shonnect.shonnect.util.Constant;
import com.squareup.okhttp.OkHttpClient;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
@Module
public class NetworkModule {
    @Provides
    @Singleton
    public UserHttpService providesUserHttpService(RestAdapter restAdapter) {
        return restAdapter.create(UserHttpService.class);
    }

    @Provides
    @Singleton
    public ChatRestHttpService providesChatRestHttpService(RestAdapter restAdapter) {
        return restAdapter.create(ChatRestHttpService.class);
    }

    @NonNull
    @Provides
    @Singleton
    public RestAdapter providesRestAdapter(@Named("api_endpoint") String endPoint, OkHttpClient okHttpClient,
            Gson gson, final UserManager userManager) {
        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setEndpoint(endPoint)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(new OkClient(okHttpClient))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        if (userManager != null && userManager.isAuthenticated()) {
                            request.addHeader("Authorization", "Bearer " + userManager.getToken());
                        }
                    }
                })
                .setConverter(new GsonConverter(gson));
        return builder.build();
    }

    @Provides
    @Singleton
    public OkHttpClient providesOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(35, TimeUnit.SECONDS);
        return okHttpClient;
    }

    @Provides
    @Singleton
    @Named("api_endpoint")
    public String providesEndpoint() {
        return Constant.API_ENDPOINT;
    }

}
