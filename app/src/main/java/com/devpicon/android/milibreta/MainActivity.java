package com.devpicon.android.milibreta;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGN_IN = 100;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // usuario logueado
            Toast.makeText(MainActivity.this, "Inicio sesion", Toast.LENGTH_SHORT).show();
            setSupportToolbar();
            setNavigationDrawer();


        } else {
            // generamos la pantalla de logueo
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setLogo(R.drawable.gdgopen_loguito)
                            .setProviders(AuthUI.GOOGLE_PROVIDER)
                            .build(), RC_SIGN_IN);
        }
    }

    private void setSupportToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.white));
        setToolbar(toolbar);
        getAppActionBar().setTitle(getString(R.string.app_name));
        getAppActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_menu_white_24dp));

    }

    private void setNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_side_menu);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_side_menu);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                MainActivity.this,
                drawerLayout, toolbar,
                R.string.app_name,
                R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(MainActivity.this);
        }
        onNavigationItemSelected(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Log.e(TAG, "Ocurrió un error durante el login");
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    /**
     * @param newConfig
     * @see <a href="https://developer.android.com/reference/android/support/v7/app/ActionBarDrawerToggle.html">ActionBarDrawerToggle</a>
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}
