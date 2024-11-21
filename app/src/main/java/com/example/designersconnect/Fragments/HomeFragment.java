package com.example.designersconnect.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.designersconnect.Adapters.PostAdapter;
import com.example.designersconnect.Models.Post;
import com.example.designersconnect.R;
import com.example.designersconnect.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    List<String> followingList;
    List<Post> postsList;
    DatabaseReference databaseReference;
    String userId;
    public HomeFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        binding.processBar.setVisibility(View.VISIBLE);
        userId = FirebaseAuth.getInstance().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        binding.tvSuggestions.setVisibility(View.GONE);
        followingList = new ArrayList<>();
        databaseReference.child("Follow").child(userId).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    followingList.add(dataSnapshot.getKey());
                }
                fetchPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }
    void fetchPosts()
    {
        postsList = new ArrayList<>();
        PostAdapter adapter = new PostAdapter(postsList, getContext(), PostAdapter.PAGE_TYPE.HOME_FRAGMENT);
        binding.rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvPosts.setAdapter(adapter);
        databaseReference.child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    Post post = dataSnapshot.getValue(Post.class);
                    for (String id: followingList)
                    {
                        if(id.equals(post.getUserId()))
                        {
                            postsList.add(post);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                fetchOtherPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    void fetchOtherPosts()
    {
        postsList = new ArrayList<>();
        PostAdapter adapter = new PostAdapter(postsList, getContext(), PostAdapter.PAGE_TYPE.HOME_FRAGMENT);
        binding.rvOtherPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvOtherPosts.setAdapter(adapter);
        databaseReference.child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    Post post = dataSnapshot.getValue(Post.class);
                    if(!followingList.contains(post.getUserId()) && !post.getUserId().equals(userId)){
                        postsList.add(post);
                    }
                }
                adapter.notifyDataSetChanged();
                binding.processBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}