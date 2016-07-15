package com.devpicon.android.milibreta.models;

/**
 * Created by armando on 7/15/16.
 */
public class Note {

    String name;

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getUid() {
        return uid;
    }

    String text;
    String uid;

    public Note(){
    }

    public Note(String name, String text, String uid){
        this.name = name;
        this.text = text;
        this.uid = uid;
    }

}
