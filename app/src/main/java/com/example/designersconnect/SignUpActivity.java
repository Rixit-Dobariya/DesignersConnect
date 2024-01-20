package com.example.designersconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.designersconnect.databinding.ActivityLoginBinding;
import com.example.designersconnect.databinding.ActivitySignUpBinding;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });
        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
    void sendData()
    {
        String email=binding.etEmail.getText().toString();
        String password=binding.etPassword.getText().toString();
        String displayName = binding.etDisplayName.getText().toString();
        String username = binding.etUsername.getText().toString();

        Intent i = new Intent(SignUpActivity.this, SignUpSecondActivity.class);
        i.putExtra("email",email);
        i.putExtra("displayName",displayName);
        i.putExtra("username",username);
        i.putExtra("password",password);

        startActivity(i);
    }
}