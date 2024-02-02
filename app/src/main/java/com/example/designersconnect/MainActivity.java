package com.example.designersconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.designersconnect.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
}