package com.shonnect.shonnect.dagger.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.shonnect.shonnect.http.deserializer.GsonIso8601Datelizer;
import com.shonnect.shonnect.manager.UserManager;
import com.shonnect.shonnect.manager.impl.UserManagerImpl;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
@Module
public class ApplicationModule {
    private Context applicationContext;

    public ApplicationModule(Context applicationContext) {
        this.applicationContext = applicationContext;
    }
    @Provides
    @Singleton
    public Context providesContext() {
        return applicationContext;
    }

    @Provides
    @Singleton
    public Gson providesGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        gsonBuilder.registerTypeAdapter(Date.class, new GsonIso8601Datelizer());
        return gsonBuilder.create();
    }
    @Provides
    @Singleton
    public SharedPreferences providesSharedPref() {
        return applicationContext.getSharedPreferences("Shonnect", Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    public UserManager providesUserManager(SharedPreferences sharedPreferences, Gson gson) {
        return new UserManagerImpl(sharedPreferences, gson);
    }


}
