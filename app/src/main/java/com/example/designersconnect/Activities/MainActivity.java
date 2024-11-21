package com.example.designersconnect.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.designersconnect.Fragments.AddPostFragment;
import com.example.designersconnect.Fragments.HomeFragment;
import com.example.designersconnect.Fragments.MessagesFragment;
import com.example.designersconnect.Fragments.MyProfileFragment;
import com.example.designersconnect.Models.UserData;
import com.example.designersconnect.R;
import com.example.designersconnect.Fragments.SearchFragment;
import com.example.designersconnect.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading data from Firebase: " + databaseError.getMessage());
            }
        });
        loadFragment(new HomeFragment(), 0);
        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.home)
                    loadFragment(new HomeFragment(), 1);
                else if(item.getItemId() == R.id.search)
                    loadFragment(new SearchFragment(), 1);
                else if(item.getItemId() == R.id.add)
                    loadFragment(new AddPostFragment(), 1);
                else if(item.getItemId() == R.id.messages)
                    loadFragment(new MessagesFragment(), 1);
                else if(item.getItemId() == R.id.profile)
                    loadFragment(new MyProfileFragment(), 1);
                return true;
            }
        });
        Intent i = getIntent();
        if(i.hasExtra("fragment")){
            binding.bottomNavigationView.setSelectedItemId(R.id.profile);
        }

    }
    void loadFragment(Fragment fragment,int flag)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if(flag == 0)
            ft.add(R.id.container,fragment);
        else
            ft.replace(R.id.container,fragment);

        ft.commit();
    }
    void setStatus(String status)
    {
        String userId = FirebaseAuth.getInstance().getUid();
        if(userId!=null) {
            FirebaseDatabase.getInstance().getReference("users").child(userId).child("status").setValue(status);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStatus("offline");
    }
}