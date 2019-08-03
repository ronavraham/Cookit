package com.grd.cookit.model.firebase;

import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
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
import java.util.stream.Collectors;

public class RecipeFirebase {
    private static RecipeFirebase instance = null;
    private static final String TAG = "RecipeFirebase";
    ValueEventListener allCollectionseventListener;
    ValueEventListener profileEventListener;


    protected RecipeFirebase() {
    }

    public static RecipeFirebase getInstance() {
        if (instance == null) {
            instance = new RecipeFirebase();
        }
        return instance;
    }

    public static void saveImage(File image, String uid, OnSuccessListener<Uri> listener,OnFailureListener onFailureListener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference imageRef = storage.getReference().child("images").child(uid);
        Uri fileUri = Uri.fromFile(image);

        UploadTask uploadTask = imageRef.putFile(fileUri);
        uploadTask.continueWithTask((task) -> {
            if (!task.isSuccessful()) {
                Exception e = task.getException();
                Log.e(TAG, e.toString());
                onFailureListener.onFailure(e);
            }
            return imageRef.getDownloadUrl();
        }).addOnSuccessListener((task)-> {
            listener.onSuccess(task);
        })
        .addOnFailureListener(onFailureListener);
    }

    public static void saveRecipe(Recipe recipe, OnSuccessListener onSuccessListener, OnFailureListener onFailureListener) {
        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
        mdatabase.child("recipes").child(recipe.getUid()).setValue(recipe).addOnSuccessListener(onSuccessListener)
                .addOnFailureListener((error) -> {
                    Log.d(TAG, error.toString());
                    onFailureListener.onFailure(error);
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

    public void getAllCollectionsForProfile(String userUid, final OnSuccessListener listener) {
        DatabaseReference fbPostsRef = FirebaseDatabase.getInstance().getReference();
        if (profileEventListener == null) {
            profileEventListener = fbPostsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DataSnapshot postsRef = dataSnapshot.child("recipes");
                    DataSnapshot usersRef = dataSnapshot.child("users");
                    List<User> users = new ArrayList<>();
                    List<Recipe> recipes = new ArrayList<>();

                    for (DataSnapshot postSnapshot : postsRef.getChildren()) {
                        Recipe r = postSnapshot.getValue(Recipe.class);
                        recipes.add(r);
                    }

                    for (DataSnapshot postSnapshot : usersRef.getChildren()) {
                        User u = postSnapshot.getValue(User.class);
                        users.add(u);
                    }

                    recipes = recipes.stream().filter(post -> post.getUserGoogleUid().equals(userUid)).collect(Collectors.toList());
                    User user = users.stream().filter(currUser -> currUser.googleUid.equals(userUid)).findFirst().get();

                    listener.onSuccess(new Pair(recipes, user));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.onSuccess(null);
                }
            });
        }
    }

    public void deletePost(String postUid) {
        // delete post image
        // delete post itself
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imagesRef = storage.getReference().child("images").child(postUid);
        imagesRef.delete();
        FirebaseDatabase.getInstance().getReference()
                .child("recipes").child(postUid).setValue(null);

    }

}
