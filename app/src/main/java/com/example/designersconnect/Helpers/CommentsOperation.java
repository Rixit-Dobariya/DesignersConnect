package com.example.designersconnect.Helpers;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CommentsOperation {
    static DatabaseReference databaseReference;
    static{
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
    public static void numberOfComments(String postId, TextView textView)
    {
        databaseReference.child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText(snapshot.getChildrenCount()+" comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void deleteComment(Context context,String postId, String commentId)
    {
        FirebaseDatabase.getInstance().getReference("Comments").child(postId).child(commentId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, "Comment deleted successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
