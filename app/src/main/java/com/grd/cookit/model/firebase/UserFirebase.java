package com.grd.cookit.model.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grd.cookit.model.entities.User;

public class UserFirebase {
    private static UserFirebase instance = null;

    protected UserFirebase() {
        // Exists only to defeat instantiation.
    }

    public static UserFirebase getInstance() {
        if (instance == null) {
            instance = new UserFirebase();
        }
        return instance;
    }

    public void getUserByUid(String uid, final OnSuccessListener<User> listener) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        usersRef.orderByChild("googleUid").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                listener.onSuccess(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onSuccess(null);
            }
        });

    }

    public void saveUser(User user) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(user.getUid()).setValue(user);
    }
}
