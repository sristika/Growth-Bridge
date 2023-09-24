package com.example.pro_hive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatAdapter extends FirestoreRecyclerAdapter<Message, ChatAdapter.ChatViewHolder> {
    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull Message model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_row, parent, false);
        return new ChatViewHolder(view);
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final TextView senderTextView;
        private final TextView messageTextView;
        private final TextView timeTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.chat_sender_text_view);
            messageTextView = itemView.findViewById(R.id.chat_message_text_view);
            timeTextView = itemView.findViewById(R.id.chat_time_text_view);
        }

        public void bind(Message message) {

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(message.getSenderId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String firstName = documentSnapshot.getString("firstName");
                                String lastName = documentSnapshot.getString("lastName");
                                String senderName = firstName + " " + lastName;
                                senderTextView.setText(senderName);
                            } else {
                                senderTextView.setText(message.getSenderId());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            senderTextView.setText(message.getSenderId());
                        }
                    });
            messageTextView.setText(message.getText());

            if(message.getTimestamp() == null){
                timeTextView.setText(FieldValue.serverTimestamp().toString());
            }else{
                timeTextView.setText(message.getTimestamp().toString());
            }

        }
    }
}

