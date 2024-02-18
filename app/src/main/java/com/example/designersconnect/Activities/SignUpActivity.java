package com.example.designersconnect.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.designersconnect.databinding.ActivitySignUpBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        if(username.contains(" "))
        {
            Toast.makeText(this, "Username cannot contain whitespace", Toast.LENGTH_SHORT).show();
        }
        else if(password.length() < 8 )
        {
            Toast.makeText(this, "Password must contain 8 or more letters", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent i = new Intent(getApplicationContext(), SignUpSecondActivity.class);
            i.putExtra("email",email);
            i.putExtra("displayName",displayName);
            i.putExtra("username",username);
            i.putExtra("password",password);

            startActivity(i);
//            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
//            usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        Toast.makeText(SignUpActivity.this, "This username is already taken by another user", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Intent i = new Intent(getApplicationContext(), SignUpSecondActivity.class);
//                        i.putExtra("email",email);
//                        i.putExtra("displayName",displayName);
//                        i.putExtra("username",username);
//                        i.putExtra("password",password);
//
//                        startActivity(i);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                }
//            });
        }
    }
}