package com.shonnect.shonnect.activity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import com.shonnect.shonnect.R;
import com.shonnect.shonnect.dagger.component.DaggerUserComponent;
import com.shonnect.shonnect.dagger.component.UserComponent;
import com.shonnect.shonnect.dagger.module.UserModule;
import com.shonnect.shonnect.http.model.UserResponse;
import com.shonnect.shonnect.manager.UserManager;
import com.shonnect.shonnect.service.UserService;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import javax.inject.Inject;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public class LoginActivity extends BaseInjectedActivity {
    @Inject
    UserService userService;
    @Inject
    UserManager userManager;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginButton loginButton = (LoginButton) findViewById(R.id.bt_fb_login);
        injectDependencies();
        if (userManager.isAuthenticated()) {
            goToMainPage();
        } else {
            loginButton.setReadPermissions("email");
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    doLogin(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException e) {
                    onLoginError();
                }
            });
        }
    }

    private void onLoginError() {
        hideLoadingDialog();
        Toast.makeText(this, R.string.login_error, Toast.LENGTH_LONG).show();
    }

    private void doLogin(AccessToken accessToken) {
        if (accessToken != null) {
            showLoadingDialog(R.string.login_loading);
            userService.loginWithFacebook(accessToken.getToken(), new UserService.OnUserLoggedInListener() {
                @Override
                public void onLoginFinished(UserResponse user, Exception ex) {
                    if (user != null) {
                        onLoginSuccess(user);
                    } else {
                        onLoginError();
                    }
                }
            });
        } else {
            onLoginError();
        }
    }



    private void onLoginSuccess(final UserResponse userResponse) {
        hideLoadingDialog();
        userManager.storeUser(userResponse.getUser(), userResponse.getToken());
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("D.Vu", "done parse install");
                    final ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();
                    currentInstallation.put("user_id", userResponse.getUser().getId());
                    currentInstallation.saveInBackground();
                } else {
                    Log.d("D.Vu", "err parse install");
                }
            }
        });
        goToMainPage();
    }

    private void goToMainPage() {
        Intent openMainIntent = new Intent(this, MainActivity.class);
        startActivity(openMainIntent);
        finish();
    }

    private void injectDependencies() {
        UserComponent userComponent = DaggerUserComponent.builder()
                                        .userModule(new UserModule())
                                        .applicationModule(getApplicatioModule())
                                        .networkModule(getNetworkModule())
                                        .build();
        userComponent.inject(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
