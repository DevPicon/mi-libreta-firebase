package com.devpicon.android.milibreta.models;

/**
 * Created by armando on 7/15/16.
 */
public class User {
    public String username;
    public String email;
    public String displayName;
    public String photoUrl;


    public User(){

    }

    public User(String username, String email, String displayName, String photoUrl){
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
    }
}
