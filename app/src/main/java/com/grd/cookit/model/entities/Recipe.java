package com.grd.cookit.model.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Recipe {

    @PrimaryKey
    @NonNull
    public String uid;
    public String name;
    public String imageUri;
    public String userGoogleUid;
    public Long timestamp;
    public double longitude;
    public double latitude;
}
