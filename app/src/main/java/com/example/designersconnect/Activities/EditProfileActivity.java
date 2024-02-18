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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.designersconnect.Models.UserData;
import com.example.designersconnect.R;
import com.example.designersconnect.databinding.ActivityEditProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfileActivity extends AppCompatActivity {
    String userId;
    Dialog customDialog;
    UserData user;
    DatabaseReference databaseReference;
    String profilePicture;
    Uri selectedImageUri;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityEditProfileBinding binding;
    String[] arr;
    String displayName;
    String userName;
    String jobTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        arr = getResources().getStringArray(R.array.stringArray);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,arr);
        binding.spEditJobTitle.setAdapter(arrayAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        fetchData();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            selectedImageUri = data.getData();
                            binding.imgProfilePhoto.setImageURI(selectedImageUri);
                        }
                    }
                });
        binding.imgProfilePhoto.setOnClickListener(v -> {
            Toast.makeText(EditProfileActivity.this, "Image clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResultLauncher.launch(intent);
        });

        binding.btnSaveProfile.setOnClickListener(v -> {
            displayName = binding.etEditDisplayName.getText().toString();
            userName = binding.etEditUsername.getText().toString();
            jobTitle = binding.spEditJobTitle.getSelectedItem().toString();
            if(displayName.isEmpty())
            {
                Toast.makeText(this, "Please enter display name", Toast.LENGTH_SHORT).show();
            }
            else if(userName.isEmpty())
            {
                Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            }
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            usersRef.orderByChild("username").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(EditProfileActivity.this, "This username is already taken by another user", Toast.LENGTH_SHORT).show();
                    } else {
                        customDialog = new Dialog(EditProfileActivity.this);
                        customDialog.setContentView(R.layout.process);
                        customDialog.setCancelable(false);
                        customDialog.show();
                        uploadImage();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        });
        binding.imageView4.setOnClickListener(v -> onBackPressed());
    }
    private void fetchData()
    {
        userId = FirebaseAuth.getInstance().getUid();
        databaseReference.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(UserData.class);
                loadData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void loadData()
    {

        Glide.with(getApplicationContext())
                .load(user.getProfilePicture())
                .apply(new RequestOptions().circleCrop())
                .into(binding.imgProfilePhoto);
        binding.etEditDisplayName.setText(user.getDisplayName());
        binding.etEditUsername.setText(user.getUsername());
        for(int i=0; i<arr.length; i++)
        {
            if(arr[i].equals(user.getJobTitle()))
            {
                binding.spEditJobTitle.setSelection(i);
            }
        }
    }
    public void updateProfilePictureInDatabase()
    {
        UserData newUserData = new UserData(userId, userName, displayName, jobTitle, profilePicture, "online");
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(userId).setValue(newUserData).addOnCompleteListener(task -> {
        if (task.isSuccessful())
        {
            Intent i = new Intent(EditProfileActivity.this, MainActivity.class);
            startActivity(i);
            customDialog.dismiss();
            finish();
        }
        else
        {
                Toast.makeText(EditProfileActivity.this, "not done", Toast.LENGTH_LONG).show();
                Exception exception = task.getException();
                if (exception != null) {

                    Log.e("FirebaseReplace", "Failed to replace user: " + exception.getMessage());
                }
            }
        });
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
                                                Toast.makeText(EditProfileActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(EditProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                UpdateUserInfoDatabase();
            }
    }

    private void UpdateUserInfoDatabase() {
        user.setUsername(userName);
        user.setDisplayName(displayName);
        user.setJobTitle(jobTitle);
        databaseReference.child("users").child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                Intent i = new Intent(EditProfileActivity.this, MainActivity.class);
                startActivity(i);
                customDialog.dismiss();
                finish();
            }
            else
            {
                Toast.makeText(EditProfileActivity.this, "not done", Toast.LENGTH_LONG).show();
                Exception exception = task.getException();
                if (exception != null) {

                    Log.e("FirebaseReplace", "Failed to replace user: " + exception.getMessage());
                }
            }
        });
    }
}