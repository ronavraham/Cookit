package com.grd.cookit.model.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

@Entity
public class User {

    @PrimaryKey
    @NonNull
    public String uid;
    public String name;
    public String profileUri;
    public String email;
    public String googleUid;

    public User(){}

    public User(FirebaseUser currentUser) {
        this.setGoogleUid(currentUser.getUid());
        this.setProfileUri(currentUser.getPhotoUrl().toString());
        this.setName(currentUser.getDisplayName());
        this.setEmail(currentUser.getEmail());
        this.setUid(UUID.randomUUID().toString());
    }

    public String getGoogleUid() {
        return googleUid;
    }

    public void setGoogleUid(String userUid) {
        this.googleUid = userUid;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public void setProfileUri(String profileUri) {
        this.profileUri = profileUri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}