package com.example.designersconnect.Helpers;

import com.example.designersconnect.Models.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MessageOperations {
     public static void deleteMessage(String messageId)
     {
         FirebaseDatabase.getInstance().getReference("messages").child(messageId).removeValue();
     }
     public static void clearChat(String userId1, String userId2){
         DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("messages");
         messagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                     Message message = messageSnapshot.getValue(Message.class);
                     if ((message.getSender().equals(userId1) && message.getReceiver().equals(userId2)) ||
                             (message.getSender().equals(userId2) && message.getReceiver().equals(userId1))) {
                         messageSnapshot.getRef().removeValue();
                     }
                 }
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {
                 System.out.println("Error deleting messages: " + databaseError.getMessage());
             }
         });
     }
}
