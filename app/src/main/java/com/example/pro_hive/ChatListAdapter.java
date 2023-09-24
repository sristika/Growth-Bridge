package com.example.pro_hive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {
    private List<Chat> chats;
    private OnFragmentInteractionListener listener;

    public ChatListAdapter(List<Chat> chats, OnFragmentInteractionListener listener) {
        this.chats = chats;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversation_list_row, parent, false);
        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chats.get(position);

        // Set the name of the chat in the chat card
        if (chat.isGroupChatfun()) {
            holder.chatNameTextView.setText(chat.getChatName()+" {Group}");
        } else {
            // If the chat is a private chat, display the name of the other user
            final String[] otherUserName = {""};
            List<String> members = chat.getMembers();
            for (String memberId : members) {
                if (!memberId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    // Get the name of the other user from Firestore and set it as the card name
                    FirebaseFirestore.getInstance().collection("users").document(memberId).get().addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.getBoolean("vc") != null && documentSnapshot.getBoolean("vc").equals(true)){
                            otherUserName[0] = documentSnapshot.getString("firstName")+" "+documentSnapshot.getString("lastName")+ " {VC}";
                        }else{
                            otherUserName[0] = documentSnapshot.getString("firstName")+" "+documentSnapshot.getString("lastName");
                        }

                        holder.chatNameTextView.setText(otherUserName[0]);
                    });
                    break;
                }
            }
        }

        // Set a click listener on the chat card to open the ChatActivity for this chat
        holder.itemView.setOnClickListener(v -> {
            listener.navigateToFragmentChatData("chat", chat.getChatId());
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView chatNameTextView;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatNameTextView = itemView.findViewById(R.id.user_name_textview);
        }


    }
}
