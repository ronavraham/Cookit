package com.grd.cookit.model.entities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class RecipeFirebase {
    private static RecipeFirebase instance = null;
    private static final String TAG = "RecipeFirebase";

    protected RecipeFirebase() {
    }

    public static RecipeFirebase getInstance() {
        if (instance == null) {
            instance = new RecipeFirebase();
        }
        return instance;
    }

    public static void saveImage(File image, String uid, final OnSuccessListener<Uri> listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference imageRef = storage.getReference().child("images").child(uid);
        Uri fileUri = Uri.fromFile(image);

        UploadTask uploadTask = imageRef.putFile(fileUri);
        uploadTask.continueWithTask((task) -> {
            if (!task.isSuccessful()) {
                Exception e = task.getException();
                Log.e(TAG, e.toString());
            }
            return imageRef.getDownloadUrl();
        }).addOnSuccessListener((task)-> {
            listener.onSuccess(task);
        });
//        uploadTask.addOnSuccessListener(taskSnapshot -> {
//            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
//            Uri downloadUrl = urlTask.getResult();
//            listener.onSuccess(downloadUrl);
//        });
    }

    public static void saveRecipe(Recipe recipe, OnSuccessListener onSuccessListener) {
        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
        mdatabase.child("recipes").child(recipe.getUid()).setValue(recipe).addOnSuccessListener(onSuccessListener)
                .addOnFailureListener((error) -> {
                    Log.d(TAG, error.toString());
                });
    }
}
