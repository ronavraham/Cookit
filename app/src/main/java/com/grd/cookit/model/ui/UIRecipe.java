package com.grd.cookit.model.ui;

import android.graphics.drawable.Drawable;

import java.util.Date;

public class UIRecipe {
    public String uid;
    public String name;
    public String userName;
    public double longitude;
    public double latitude;
    public Date timestamp;
    public Drawable RecipeImage;
    public Drawable ProfileImage;

    public Date getTimestamp() {
        return timestamp;
    }

}
