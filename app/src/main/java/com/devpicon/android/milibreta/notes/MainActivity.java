package com.devpicon.android.milibreta.notes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.devpicon.android.milibreta.BaseActivity;
import com.devpicon.android.milibreta.Constants;
import com.devpicon.android.milibreta.R;
import com.devpicon.android.milibreta.addNote.AddNoteDialogFragment;
import com.devpicon.android.milibreta.addNote.NoteFirebaseRecyclerAdapter;
import com.devpicon.android.milibreta.app.MiLibretaApplication;
import com.devpicon.android.milibreta.login.LoginActivity;
import com.devpicon.android.milibreta.models.Note;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity
        extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        EasyPermissions.PermissionCallbacks {
    ;
    private static final int RC_TAKE_PICTURE = 101;
    private static final int RC_STORAGE_AND_CAMERA_PERMS = 102;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_CAMERA_PERMS = 103;

    private Uri fileUri = null;
    private Uri downloadUrl = null;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private DatabaseReference notesDatabaseReference;
    private StorageReference photoStorageReference;
    private NoteFirebaseRecyclerAdapter noteFirebaseRecyclerAdapter;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MiLibretaApplication application = (MiLibretaApplication) getApplicationContext();
        photoStorageReference = application.getPhotoStorageReference();
        notesDatabaseReference = application.getChildNoteReference();
        sharedPreferences = getApplication().getSharedPreferences(Constants.MI_LIBRETA_PREFERENCES, Context.MODE_PRIVATE);

        // usuario logueado
        showMessageToast("Inicio sesion");
        setSupportToolbar();
        setNavigationDrawer();
        setNavigationHeader();
        setFirebaseRecyclerView();
        setAddNoteFAB();
        setAddPictureFAB();


    }

    private void setAddNoteFAB() {
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add_note);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessageToast("nota añadida!!");
                // Create and show the dialog.
                AddNoteDialogFragment dialog = new AddNoteDialogFragment();
                dialog.show(getFragmentManager(), AddNoteDialogFragment.class.getSimpleName());


            }
        });
    }

    private void setAddPictureFAB() {
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add_picture);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
    }

    @AfterPermissionGranted(RC_STORAGE_AND_CAMERA_PERMS)
    private void launchCamera() {
        Log.d(TAG, "launchCamera");

        // Check that we have permission to read images from external storage.
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_storage_camera),
                    RC_STORAGE_AND_CAMERA_PERMS, permissions);
            return;
        }


        // Create intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Choose file storage location
        File file = new File(Environment.getExternalStorageDirectory(), UUID.randomUUID().toString() + ".jpg");
        fileUri = Uri.fromFile(file);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // Launch intent
        startActivityForResult(takePictureIntent, RC_TAKE_PICTURE);

    }

    private void setFirebaseRecyclerView() {
        // Implementacion de FirebaseUI-Database
        RecyclerView noteRecyclerView = (RecyclerView) findViewById(R.id.note_recycler_view);
        noteRecyclerView.setHasFixedSize(true);
        noteRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        noteFirebaseRecyclerAdapter = new NoteFirebaseRecyclerAdapter(notesDatabaseReference);
        noteRecyclerView.setAdapter(noteFirebaseRecyclerAdapter);
    }

    private void setNavigationHeader() {

        View headerView = navigationView.getHeaderView(0);
        ImageView imageViewHeader = (ImageView) headerView.findViewById(R.id.imageViewHeader);
        TextView textViewNameHeader = (TextView) headerView.findViewById(R.id.textViewNameHeader);
        TextView textViewEmailHeader = (TextView) headerView.findViewById(R.id.textViewEmailHeader);


        //TODO: Fix & Refactor
        textViewNameHeader.setText(sharedPreferences.getString(Constants.PREFERENCE_DISPLAY_NAME, "Desconocido"));
        textViewEmailHeader.setText(sharedPreferences.getString(Constants.PREFERENCE_EMAIL, ""));

        String avatarUrl = sharedPreferences.getString(Constants.PREFERENCE_PHOTO_URI, null);

        if (avatarUrl != null) {
            Glide.with(this)
                    .load(avatarUrl)
                    .asBitmap()
                    .transform(new CropCircleTransformation(this))
                    .into(imageViewHeader);
        } else {
            Glide.with(this)
                    .load(R.mipmap.ic_launcher)
                    .asBitmap()
                    .transform(new CropCircleTransformation(this))
                    .into(imageViewHeader);
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

        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                if (fileUri != null) {
                    Log.d(TAG, "Taking picture succeded");
                    uploadFromUri(fileUri);
                } else {
                    Log.w(TAG, "File URI is null");
                }
            } else {
                showMessageToast("Taking picture failed.");
            }
        }
    }

    private void uploadFromUri(Uri fileUri) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        final StorageReference photoReference = photoStorageReference.child("photos")
                .child(fileUri.getLastPathSegment());
        showProgressDialog();
        Log.d(TAG, "uploadFromUri:dst:" + photoReference.getPath());
        photoReference.putFile(fileUri).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "uploadFromUri:onFailure", e);
                downloadUrl = null;
                hideProgressDialog();
                String message = "Error: upload failed";
                showMessageToast(message);

            }
        }).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "uploadFromUri:onSuccess");
                // Get the public download URL
                downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                savePictureUrlAsANote(downloadUrl);
                showMessageToast("upload completed! " + downloadUrl.toString());
                hideProgressDialog();
            }
        });
    }

    private void savePictureUrlAsANote(Uri downloadUrl) {

        //TODO: Corregir a  una mejor forma de obtener el timestamp
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(new Date());

        notesDatabaseReference.push().setValue(
                new Note(
                        sharedPreferences.getString(Constants.PREFERENCE_DISPLAY_NAME, "Desconocido"),
                        "Foto",
                        downloadUrl.toString(),
                        sharedPreferences.getString(Constants.PREFERENCE_UID, null),
                        formattedDate,
                        sharedPreferences.getString(Constants.PREFERENCE_PHOTO_URI, "")));
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
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (noteFirebaseRecyclerAdapter != null) {
            noteFirebaseRecyclerAdapter.cleanup();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    private void showMessageToast(String message) {
        Toast.makeText(MainActivity.this, message,
                Toast.LENGTH_SHORT).show();
    }
}
