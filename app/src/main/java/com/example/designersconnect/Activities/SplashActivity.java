package com.example.designersconnect.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.designersconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                FirebaseDatabase.getInstance().getReference().keepSynced(true);
                Intent i;
                if(currentUser != null){
                    i = new Intent(SplashActivity.this, MainActivity.class);
                }
                else
                {
                    i = new Intent(SplashActivity.this, WelcomeActivity.class);
                }
                startActivity(i);
                finish();
            }
        }, 3000);
    }
}