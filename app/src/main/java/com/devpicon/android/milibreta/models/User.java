package com.devpicon.android.milibreta.models;

/**
 * Created by armando on 7/15/16.
 */
public class User {
    public String username;
    public String email;
    public String displayName;
    public String photoUrl;
    public String uid;


    public User() {

    }

    public User(String username, String email, String displayName, String uid, String photoUrl) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.uid = uid;
    }

    public User(String username, String email, String displayName, String uid) {
        this(username, email, displayName, uid, null);
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
