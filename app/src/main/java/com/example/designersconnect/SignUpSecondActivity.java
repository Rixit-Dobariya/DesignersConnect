package com.example.designersconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.designersconnect.databinding.ActivitySignUpSecondBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpSecondActivity extends AppCompatActivity {


    ActivitySignUpSecondBinding binding;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpSecondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.stringArray));
        binding.spJobTitles.setAdapter(arrayAdapter);

        mAuth = FirebaseAuth.getInstance();
        binding.progressbar1.setVisibility(View.GONE);

        binding.btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                binding.progressbar1.setVisibility(View.VISIBLE);
                Intent i = getIntent();
                String email = i.getStringExtra("email");
                String password = i.getStringExtra("password");
                String displayName = i.getStringExtra("displayName");
                String username = i.getStringExtra("username");
                String jobTitle = binding.spJobTitles.getSelectedItem().toString();


                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference = database.getReference("users");
                                if (task.isSuccessful())
                                {
                                    String userId = mAuth.getCurrentUser().getUid();
                                    UserData newUserData = new UserData(userId, username, displayName, jobTitle);
                                    databaseReference.child(userId).setValue(newUserData);
                                    binding.progressbar1.setVisibility(View.GONE);

                                    Intent i = new Intent(SignUpSecondActivity.this, MainActivity.class);
                                    startActivity(i);
                                    binding.progressbar1.setVisibility(View.GONE);
                                    finish();
                                    Toast.makeText(SignUpSecondActivity.this, "done", Toast.LENGTH_LONG).show();
                                } else
                                {
                                    Toast.makeText(SignUpSecondActivity.this, "not done", Toast.LENGTH_SHORT).show();
                                    binding.progressbar1.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });
    }
}