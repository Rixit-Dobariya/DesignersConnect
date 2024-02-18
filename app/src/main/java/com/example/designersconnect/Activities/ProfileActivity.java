package com.example.designersconnect.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.designersconnect.Adapters.PhotoAdapter;
import com.example.designersconnect.Helpers.FollowOperations;
import com.example.designersconnect.Models.Photo;
import com.example.designersconnect.Models.UserData;
import com.example.designersconnect.R;
import com.example.designersconnect.databinding.ActivityProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    List<Photo> photos = new ArrayList<>();
    Dialog customDialog;
    ActivityProfileBinding binding;
    DatabaseReference databaseReference;
    String userId;
    PhotoAdapter photoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference();
        Intent i = getIntent();
        userId = i.getStringExtra("userId");

        FollowOperations.followText(binding.btnProfileFollow, userId);
        binding.btnProfileFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowOperations.follow(binding.btnProfileFollow,userId);
            }
        });
        setProfileData();
        binding.btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                i.putExtra("userId",userId);
                startActivity(i);
            }
        });
        binding.imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setPosts();

    }
    private void setPosts() {
        photoAdapter = new PhotoAdapter(photos, getApplicationContext(), PhotoAdapter.page.PROFILEACTIVITY,userId);
        binding.rvPosts.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        binding.rvPosts.setAdapter(photoAdapter);
        Query query = databaseReference.child("posts").orderByChild("userId").equalTo(userId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                photos.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String picture = postSnapshot.child("postPicture").getValue(String.class);
                    photos.add(new Photo(picture));
                }
                photoAdapter = new PhotoAdapter(photos,getApplicationContext(),PhotoAdapter.page.PROFILEACTIVITY,userId);
                binding.rvPosts.setAdapter(photoAdapter);
//                Toast.makeText(getContext(), ""+new PhotoAdapter(photos,getContext()).getItemCount(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void setProfileData()
    {
        databaseReference.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    UserData data = dataSnapshot.getValue(UserData.class);
                    binding.myProfile.setText(data.getDisplayName());
                    binding.tvUsername.setText(data.getUsername());
                    binding.btnJobTitle.setText(data.getJobTitle());
                    binding.followersInfo.setText("0 Followers | 0 Following");
                    RequestOptions requestOptions = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(getApplicationContext())
                            .load(data.getProfilePicture())
                            .apply(requestOptions)
                            .into(binding.imgProfilePhoto);
                }
                else
                {
                    Log.d("Firebase", "No data found at the specified path");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading data from Firebase: " + databaseError.getMessage());
            }
        });
    }

}