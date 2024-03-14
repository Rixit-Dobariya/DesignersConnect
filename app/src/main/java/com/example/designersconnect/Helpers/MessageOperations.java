package com.example.designersconnect.Helpers;

import com.google.firebase.database.FirebaseDatabase;

public class MessageOperations {
     public static void deleteMessage(String messageId)
     {
         FirebaseDatabase.getInstance().getReference("messages").child(messageId).removeValue();
     }
}
