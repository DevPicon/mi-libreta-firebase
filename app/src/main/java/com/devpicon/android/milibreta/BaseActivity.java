package com.devpicon.android.milibreta;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by armando on 7/14/16.
 */
public class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActionBar appActionBar;

    private ProgressDialog progressDialog;

    protected void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Cargando...");
            progressDialog.setIndeterminate(true);
        }
    }

    protected void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public Toolbar getToolbar() {

        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        appActionBar = getSupportActionBar();
        if (appActionBar != null) {
            appActionBar.setDisplayHomeAsUpEnabled(true);
            appActionBar.setHomeButtonEnabled(true);
        }
    }

    public ActionBar getAppActionBar() {
        return appActionBar;
    }

}
