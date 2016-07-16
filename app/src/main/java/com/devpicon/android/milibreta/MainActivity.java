package com.devpicon.android.milibreta;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devpicon.android.milibreta.holders.NoteFirebaseRecyclerAdapter;
import com.devpicon.android.milibreta.models.Note;
import com.devpicon.android.milibreta.models.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGN_IN = 100;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private NoteFirebaseRecyclerAdapter noteFirebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            // usuario logueado
            Toast.makeText(MainActivity.this, "Inicio sesion", Toast.LENGTH_SHORT).show();
            setSupportToolbar();
            setNavigationDrawer();
            setNavigationHeader();
            setFirebaseRecyclerView();

            FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add_note);
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "nota añadida!!", Toast.LENGTH_SHORT).show();
                    databaseReference.child("notes").push().setValue(
                            new Note(
                                    firebaseAuth.getCurrentUser().getDisplayName(),
                                    "Una nota",
                                    firebaseAuth.getCurrentUser().getUid()));
                }
            });


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

    private void setFirebaseRecyclerView() {
        // Implementacion de FirebaseUI-Database
        RecyclerView noteRecyclerView = (RecyclerView) findViewById(R.id.note_recycler_view);
        noteRecyclerView.setHasFixedSize(true);
        noteRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        noteFirebaseRecyclerAdapter = new NoteFirebaseRecyclerAdapter(databaseReference.child("notes"));
        noteRecyclerView.setAdapter(noteFirebaseRecyclerAdapter);
    }

    private void setNavigationHeader() {

        View headerView = navigationView.getHeaderView(0);
        ImageView imageViewHeader = (ImageView) headerView.findViewById(R.id.imageViewHeader);
        TextView textViewNameHeader = (TextView) headerView.findViewById(R.id.textViewNameHeader);
        TextView textViewEmailHeader = (TextView) headerView.findViewById(R.id.textViewEmailHeader);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        textViewNameHeader.setText(currentUser.getDisplayName());
        textViewEmailHeader.setText(currentUser.getEmail());
        Glide.with(this)
                .load(currentUser.getPhotoUrl())
                .asBitmap()
                .transform(new CropCircleTransformation(this))
                .into(imageViewHeader);

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
        navigationView = (NavigationView) findViewById(R.id.nav_side_menu);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                MainActivity.this,
                drawerLayout, toolbar,
                R.string.app_name,
                R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(MainActivity.this);
        }
        //onNavigationItemSelected(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                User user = new User(
                        usernameFromEmail(currentUser.getEmail()),
                        currentUser.getEmail(),
                        currentUser.getDisplayName(),
                        currentUser.getPhotoUrl().toString());

                databaseReference.child("users").child(currentUser.getUid()).setValue(user);

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

        switch (item.getItemId()) {
            case R.id.nav_item_logout:
                signOut();
                break;
            default:
                break;

        }

        drawerLayout.closeDrawers();
        return true;
    }

    private void signOut() {
        AuthUI.getInstance().signOut(MainActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    /**
     * @param newConfig
     * @see <a href="https://developer.android.com/reference/android/support/v7/app/ActionBarDrawerToggle.html">ActionBarDrawerToggle</a>
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (actionBarDrawerToggle != null) {
            actionBarDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (actionBarDrawerToggle != null) {
            actionBarDrawerToggle.syncState();
        }
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noteFirebaseRecyclerAdapter.cleanup();
    }
}
