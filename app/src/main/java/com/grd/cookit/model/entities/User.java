package com.grd.cookit.model.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

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
    public String userUid;

    public User(){}

    public User(FirebaseUser currentUser) {
        this.setUserUid(currentUser.getUid());
        this.setProfileUri(currentUser.getPhotoUrl().toString());
        this.setName(currentUser.getDisplayName());
        this.setEmail(currentUser.getEmail());
        this.setUid(UUID.randomUUID().toString());
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
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