package com.shonnect.shonnect.activity;

import com.shonnect.shonnect.R;
import com.shonnect.shonnect.dagger.module.ApplicationModule;
import com.shonnect.shonnect.dagger.module.NetworkModule;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.BranchException;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public class BaseInjectedActivity extends AppCompatActivity implements DialogInterface.OnCancelListener {

    private ProgressDialog progressDialog;
    private boolean isShowingProgressDialog = false;

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    protected NetworkModule getNetworkModule() {
        return new NetworkModule();
    }

    protected ApplicationModule getApplicatioModule() {
        return new ApplicationModule(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.loading_text));
    }


    protected void showLoadingDialog() {
        showLoadingDialog(R.string.loading_text);
    }

    protected void showLoadingDialog(boolean isCancelable) {
        showLoadingDialog(R.string.loading_text, isCancelable);
    }

    protected void showLoadingDialog(int resId) {
        showLoadingDialog(resId, false);
    }

    protected void showLoadingDialog(int resId, boolean isCancelable) {
        if (progressDialog != null && !progressDialog.isShowing() && !isFinishing()) {
            isShowingProgressDialog = true;
            progressDialog.setCancelable(isCancelable);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(getString(resId));
            if (isCancelable) {
                progressDialog.setOnCancelListener(this);
            } else {
                progressDialog.setOnCancelListener(null);
            }
            progressDialog.show();
        }
    }

    protected void hideLoadingDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        isShowingProgressDialog = false;
    }

    @Override
    protected void onStart() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onStart();
        Branch branch = null;
        try {
            branch = Branch.getInstance();
            branch.initSession(new Branch.BranchReferralInitListener() {
                @Override
                public void onInitFinished(JSONObject referringParams, BranchError error) {
                    if (error == null) {
                        // params are the deep linked params associated with the link that the user clicked before showing up
                        Log.i("BranchConfigTest", "deep link data: " + referringParams.toString());
                    }
                }
            }, this.getIntent().getData(), this);
        } catch (BranchException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }
}
