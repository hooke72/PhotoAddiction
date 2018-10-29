package com.example.hooke.photoaddiction.models;

import android.net.Uri;

public class Photo {

    private String time;
    private Uri uri;

    public Photo(String time, Uri uri) {
        this.time = time;
        this.uri = uri;
    }

    public String getTime() {
        return time;
    }

    public Uri getUri() {
        return uri;
    }
}
