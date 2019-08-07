package com.grd.cookit.model.ui;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.squareup.picasso.RequestCreator;

import java.util.Date;

public class UIRecipe {
    public String uid;
    public String name;
    public String description;
    public String userName;
    public String userGoogleId;
    public double longitude;
    public double latitude;
    public Date timestamp;
    public Drawable recipeImage;
    public RequestCreator recipeImageRequestCreator;
    public String imageUri;
    public Drawable userProfileImage;

    public Date getTimestamp() {
        return timestamp;
    }

}
