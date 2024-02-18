package com.example.designersconnect.Helpers;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.designersconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LikesOperations {
    static String selfUserId;
    static DatabaseReference databaseReference;
    static{
        databaseReference = FirebaseDatabase.getInstance().getReference();
        selfUserId = FirebaseAuth.getInstance().getUid();
    }
    public static void isLiked(String postId, ImageView imageView)
    {
        databaseReference.child("Likes").child(postId).child(selfUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    imageView.setImageResource(R.drawable.baseline_favorite_24_red);
                }
                else
                {
                    imageView.setImageResource(R.drawable.baseline_favorite_border_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void numberOfLikes(String postId, TextView textView)
    {
        databaseReference.child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText(snapshot.getChildrenCount()+" Likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void like(String postId, ImageView imageView)
    {
        if(imageView.getTag().equals("like"))
        {
            databaseReference.child("Likes").child(postId).child(selfUserId).setValue(true);
            imageView.setImageResource(R.drawable.baseline_favorite_24_red);
            imageView.setTag("liked");
        }
        else
        {
            imageView.setImageResource(R.drawable.baseline_favorite_border_24);
            databaseReference.child("Likes").child(postId).child(selfUserId).removeValue();
            imageView.setTag("like");
        }
    }
}
