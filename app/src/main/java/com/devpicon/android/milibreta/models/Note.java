package com.devpicon.android.milibreta.models;

/**
 * Created by armando on 7/15/16.
 */
public class Note {

    public String pictureUrl;
    public String name;
    public String text;
    public String uid;
    public String timestamp;
    public String userImageUrl;

    public Note() {
    }

    public Note(String name, String text, String uid, String timestamp, String userImageUrl) {
        this.name = name;
        this.text = text;
        this.pictureUrl = null;
        this.uid = uid;
        this.timestamp = timestamp;
        this.userImageUrl = userImageUrl;
    }

    public Note(String name, String text, String pictureUrl, String uid, String timestamp, String userImageUrl) {
        this.name = name;
        this.text = text;
        this.pictureUrl = pictureUrl;
        this.uid = uid;
        this.timestamp = timestamp;
        this.userImageUrl = userImageUrl;
    }



    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getUid() {
        return uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }
}
