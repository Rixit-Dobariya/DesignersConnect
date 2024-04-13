package com.example.designersconnect.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.designersconnect.Adapters.PhotoAdapter;
import com.example.designersconnect.Adapters.PostAdapter;
import com.example.designersconnect.Models.Photo;
import com.example.designersconnect.Models.Post;
import com.example.designersconnect.databinding.ActivityMyPostsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyPostsActivity extends AppCompatActivity {

    ActivityMyPostsBinding binding;
    int selectedPostId;
    List<Post> posts;
    DatabaseReference databaseReference;
    PostAdapter postAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyPostsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();
        selectedPostId = i.getIntExtra("position",0);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("posts").orderByChild("userId").equalTo(FirebaseAuth.getInstance().getUid());
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(posts,getApplicationContext(), PostAdapter.PAGE_TYPE.MY_POSTS_ACTIVITY);
        binding.rvAllPosts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.rvAllPosts.setAdapter(postAdapter);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    posts.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        binding.rvAllPosts.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                binding.rvAllPosts.removeOnLayoutChangeListener(this);
                binding.rvAllPosts.scrollToPosition(selectedPostId);
            }
        });
    }

}