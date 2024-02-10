package com.example.designersconnect.Helpers;

import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FollowOperations {
    static String selfUserId;
    static DatabaseReference databaseReference;
    static{
        databaseReference = FirebaseDatabase.getInstance().getReference();
        selfUserId = FirebaseAuth.getInstance().getUid();
    }
    public static void followText(AppCompatButton btnFollow){
        databaseReference.child("Follow").child(selfUserId).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    btnFollow.setText("Following");
                else
                    btnFollow.setText("Follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void follow(AppCompatButton btnFollow, String userId){
        if(btnFollow.getText().toString().equals("Follow"))
        {
            databaseReference.child("Follow").child(selfUserId)
                    .child("following").child(userId).setValue(true);
            databaseReference.child("Follow").child(userId)
                    .child("followers").child(selfUserId).setValue(true);
        }
        else
        {
            databaseReference.child("Follow").child(selfUserId)
                    .child("following").child(userId).removeValue();
            databaseReference.child("Follow").child(userId)
                    .child("followers").child(selfUserId).removeValue();
        }
    }
}
