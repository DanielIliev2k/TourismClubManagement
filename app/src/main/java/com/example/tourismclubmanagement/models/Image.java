package com.example.tourismclubmanagement.models;

import android.net.Uri;

public class Image {
    private Uri uri;
    private String reference;

    public Image(Uri uri, String reference) {
        this.uri = uri;
        this.reference = reference;
    }

    public Image() {
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
