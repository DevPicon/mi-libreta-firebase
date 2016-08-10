package com.devpicon.android.milibreta.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.devpicon.android.milibreta.Constants;
import com.devpicon.android.milibreta.R;
import com.devpicon.android.milibreta.app.MiLibretaApplication;
import com.devpicon.android.milibreta.models.User;
import com.devpicon.android.milibreta.notes.MainActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MiLibretaApplication application = (MiLibretaApplication) getApplicationContext();
        firebaseAuth = application.getFirebaseAuth();
        userDatabaseReference = application.getChildUserReference();

        login();
    }


    /**
     * https://developer.android.com/reference/android/content/Intent.html#FLAG_ACTIVITY_NEW_TASK
     */
    private void login() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {

            User user = getUser(currentUser);
            saveUserIntoDatabase(user);
            saveUserDataIntoPreferences(user);

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            // generamos la pantalla de logueo
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setLogo(R.drawable.milibreta_logo)
                            .setProviders(
                                    AuthUI.GOOGLE_PROVIDER,
                                    AuthUI.EMAIL_PROVIDER)
                            .build(), RC_SIGN_IN);
        }
    }

    private void saveUserDataIntoPreferences(User user) {
        SharedPreferences preferences =
                getApplication().getSharedPreferences(Constants.MI_LIBRETA_PREFERENCES, Context.MODE_PRIVATE);

        preferences.edit().putString(Constants.PREFERENCE_DISPLAY_NAME, user.getDisplayName()).apply();
        preferences.edit().putString(Constants.PREFERENCE_PHOTO_URI, user.getPhotoUrl()).apply();
        preferences.edit().putString(Constants.PREFERENCE_UID, user.getUid()).apply();
        preferences.edit().putString(Constants.PREFERENCE_EMAIL, user.getEmail()).apply();
    }

    private void saveUserIntoDatabase(final User currentUser) {

        userDatabaseReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user == null) {
                    user = currentUser;
                    userDatabaseReference.child(currentUser.getUid()).setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });

    }

    @NonNull
    private User getUser(FirebaseUser currentUser) {
        User user;
        Uri photoUrl = currentUser.getPhotoUrl();
        String userName = usernameFromEmail(currentUser.getEmail());
        if (photoUrl != null) {
            photoUrl = currentUser.getPhotoUrl();
            user = new User(
                    userName,
                    currentUser.getEmail(),
                    currentUser.getDisplayName(),
                    currentUser.getUid(),
                    photoUrl.toString());
        } else {
            user = new User(
                    userName,
                    currentUser.getEmail(),
                    currentUser.getDisplayName(),
                    currentUser.getUid());
        }
        return user;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                login();
                finish();
            }
        } else {
            Log.e(TAG, "Ocurrió un error durante el login");
        }
    }


    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

}
