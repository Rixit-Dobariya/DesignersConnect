package com.example.designersconnect.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.designersconnect.Models.UserData;
import com.example.designersconnect.R;
import com.example.designersconnect.databinding.ActivitySignUpSecondBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUpSecondActivity extends AppCompatActivity {

    ActivitySignUpSecondBinding binding;
    FirebaseAuth mAuth;
    Uri selectedImageUri;
    String profilePicture, userId,email,password,displayName,username,jobTitle;
    ActivityResultLauncher<Intent> activityResultLauncher;
    DatabaseReference databaseReference;
    Dialog customDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpSecondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.stringArray));
        binding.spJobTitles1.setAdapter(arrayAdapter);

        mAuth = FirebaseAuth.getInstance();
        binding.progressbar1.setVisibility(View.GONE);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            selectedImageUri = data.getData();
                            binding.imageProfileInput.setImageURI(selectedImageUri);
                        }
                    }
                });
        binding.imageProfileInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignUpSecondActivity.this, "Image clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResultLauncher.launch(intent);
            }
        });
        binding.btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

//                customDialog = new Dialog(getApplicationContext());
//                customDialog.setContentView(R.layout.process);
//                customDialog.setCancelable(false);
//                customDialog.show();
                Intent i = getIntent();
                email = i.getStringExtra("email");
                password = i.getStringExtra("password");
                displayName = i.getStringExtra("displayName");
                username = i.getStringExtra("username");
                jobTitle = binding.spJobTitles1.getSelectedItem().toString();



                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                 databaseReference = database.getReference("users");
                                if (task.isSuccessful())
                                {
                                    userId = mAuth.getCurrentUser().getUid();
                                    uploadImage();
                                }
                                else
                                {
                                    Toast.makeText(SignUpSecondActivity.this, "not done", Toast.LENGTH_SHORT).show();
//                                    customDialog.dismiss();
                                }
                            }
                        });
            }
        });
    }
    public void updateProfilePictureInDatabase()
    {
        UserData newUserData = new UserData(userId, username, displayName, jobTitle, profilePicture);
        databaseReference.child(userId).setValue(newUserData);

        Intent i = new Intent(SignUpSecondActivity.this, MainActivity.class);
//        customDialog.dismiss();
        startActivity(i);

        finish();
        Toast.makeText(SignUpSecondActivity.this, "done", Toast.LENGTH_LONG).show();
    }
    private void uploadImage() {
        if (selectedImageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageReference.child("images/" + System.currentTimeMillis() + ".jpg");

            imageRef.putFile(selectedImageUri)
                    .addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Uri downloadUri = task.getResult();
                                            profilePicture=downloadUri.toString();
                                            updateProfilePictureInDatabase();
                                        } else {
                                            Toast.makeText(SignUpSecondActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(SignUpSecondActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }
}