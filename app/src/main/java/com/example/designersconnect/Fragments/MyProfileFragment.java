package com.example.designersconnect.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.designersconnect.Activities.FollowersActivity;
import com.example.designersconnect.Activities.SettingsActivity;
import com.example.designersconnect.Adapters.PhotoAdapter;
import com.example.designersconnect.Activities.EditProfileActivity;
import com.example.designersconnect.Models.Photo;
import com.example.designersconnect.Models.UserData;
import com.example.designersconnect.R;
import com.example.designersconnect.databinding.FragmentMyProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyProfileFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference databaseReference;
    Dialog customDialog;
    String uid;
    List<Photo> photos = new ArrayList<>();
    PhotoAdapter photoAdapter;


    private FragmentMyProfileBinding binding;
    public MyProfileFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false);

        customDialog = new Dialog(getActivity());
        customDialog.setContentView(R.layout.process);
        customDialog.setCancelable(false);
        customDialog.show();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid=currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        setProfileData();
        setPosts();

        binding.followersInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getContext(), FollowersActivity.class);
                i.putExtra("userId", uid);
                startActivity(i);
            }
        });
        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(i);
            }
        });

        binding.imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);
            }
        });

        return binding.getRoot();
    }
    private void setPosts() {
        photoAdapter = new PhotoAdapter(photos,getActivity(), PhotoAdapter.page.MYPROFILEFRAGMENT,FirebaseAuth.getInstance().getUid());
        binding.rvPosts.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.rvPosts.setAdapter(photoAdapter);
        Query query = databaseReference.child("posts").orderByChild("userId").equalTo(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                photos.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String picture = postSnapshot.child("postPicture").getValue(String.class);
                    photos.add(new Photo(picture));
                }
                photoAdapter = new PhotoAdapter(photos,getContext(), PhotoAdapter.page.MYPROFILEFRAGMENT,FirebaseAuth.getInstance().getUid());
                binding.rvPosts.setAdapter(photoAdapter);
//                Toast.makeText(getContext(), ""+new PhotoAdapter(photos,getContext()).getItemCount(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setProfileData()
    {
        databaseReference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    UserData data = dataSnapshot.getValue(UserData.class);
                    binding.tvDisplayName.setText(data.getDisplayName());
                    binding.tvUsername.setText(data.getUsername());
                    binding.btnJobTitle.setText(data.getJobTitle());
                    binding.followersInfo.setText("0 Followers | 0 Following");
                    RequestOptions requestOptions = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    if (getContext() != null && data.getProfilePicture() != null) {
                        Glide.with(getContext())
                                .load(data.getProfilePicture())
                                .apply(requestOptions)
                                .into(binding.imgProfilePhoto);
                    }

                    customDialog.dismiss();
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