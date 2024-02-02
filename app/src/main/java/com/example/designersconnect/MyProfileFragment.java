package com.example.designersconnect;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.designersconnect.EditProfileActivity;
import com.example.designersconnect.R;
import com.example.designersconnect.SettingsActivity;
import com.example.designersconnect.Models.UserData;
import com.example.designersconnect.databinding.FragmentMyProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyProfileFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference databaseReference;
    Dialog customDialog;


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
        String uid=currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    Glide.with(getActivity())
                            .load(data.getProfilePicture())
                            .apply(requestOptions)
                            .into(binding.imgProfilePhoto);
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
    
}