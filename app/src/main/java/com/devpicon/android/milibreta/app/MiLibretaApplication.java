package com.devpicon.android.milibreta.app;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by armando on 7/30/16.
 */
public class MiLibretaApplication extends Application {

    public static final String STORAGE_PHOTOS = "photos";
    private static String CHILD_NOTES = "notes";
    private static String CHILD_USERS = "users";

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public FirebaseAuth getFirebaseAuth() {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }

    public DatabaseReference getChildNoteReference() {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }

        return databaseReference.child(CHILD_NOTES);
    }

    public DatabaseReference getChildUserReference() {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }

        return databaseReference.child(CHILD_USERS);
    }

    public StorageReference getPhotoStorageReference() {
        if (storageReference == null) {
            storageReference = FirebaseStorage.getInstance().getReference();
        }
        return storageReference.child(STORAGE_PHOTOS);
    }
}
