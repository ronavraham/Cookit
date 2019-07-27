package com.grd.cookit.model.entities;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class RecipeFirebase {
    private static RecipeFirebase instance = null;

    protected RecipeFirebase() {
    }

    public static RecipeFirebase getInstance() {
        if (instance == null) {
            instance = new RecipeFirebase();
        }
        return instance;
    }

    public static void saveImage(Bitmap image, String uid, final OnSuccessListener<Uri> listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference imageRef = storage.getReference().child("images").child(uid);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, buffer);
        byte[] data = buffer.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!urlTask.isSuccessful()) {
                Uri downloadUrl = urlTask.getResult();
                listener.onSuccess(downloadUrl);
            }
        });
    }

    public static void saveRecipe(Recipe recipe, OnSuccessListener onSuccessListener) {
        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
        mdatabase.child("recipes").child(recipe.getUid()).setValue(recipe).addOnSuccessListener(onSuccessListener);
    }
}
