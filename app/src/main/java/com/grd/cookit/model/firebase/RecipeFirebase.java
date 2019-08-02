package com.grd.cookit.model.firebase;

import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grd.cookit.model.entities.Recipe;
import com.grd.cookit.model.entities.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecipeFirebase {
    private static RecipeFirebase instance = null;
    private static final String TAG = "RecipeFirebase";
    ValueEventListener allCollectionseventListener;


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
        mdatabase.child("recipes").child(recipe.uid).setValue(recipe).addOnSuccessListener(onSuccessListener)
                .addOnFailureListener((error) -> {
                    Log.d(TAG, error.toString());
                });
    }

    public void getAllCollections(final OnSuccessListener listener) {
        DatabaseReference fbPostsRef = FirebaseDatabase.getInstance().getReference();
        if (allCollectionseventListener == null) {
            allCollectionseventListener = fbPostsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DataSnapshot postsRef = dataSnapshot.child("recipes");
                    DataSnapshot usersRef = dataSnapshot.child("users");
                    List<User> users = new ArrayList<>();
                    List<Recipe> posts = new ArrayList<>();

                    for (DataSnapshot postSnapshot : postsRef.getChildren()) {
                        Recipe r = postSnapshot.getValue(Recipe.class);
                        posts.add(r);
                    }

                    for (DataSnapshot postSnapshot : usersRef.getChildren()) {
                        User u = postSnapshot.getValue(User.class);
                        users.add(u);
                    }

                    listener.onSuccess(new Pair(posts, users));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.onSuccess(null);
                }
            });
        }
    }

}
