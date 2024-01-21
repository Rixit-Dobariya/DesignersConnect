package com.example.designersconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;

import com.example.designersconnect.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadFragment(new HomeFragment(), 1);
        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.home)
                    loadFragment(new HomeFragment(), 0);
                else if(item.getItemId() == R.id.search)
                    loadFragment(new HomeFragment(), 0);
                else if(item.getItemId() == R.id.add)
                    showAddDialog();
                else if(item.getItemId() == R.id.messages)
                    loadFragment(new MessagesFragment(), 0);
                else if(item.getItemId() == R.id.profile)
                    loadFragment(new MyProfileFragment(), 0);
                return true;
            }
        });
    }
    void showAddDialog()
    {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.add_dialog);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);

        dialog.show();
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