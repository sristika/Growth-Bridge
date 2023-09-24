package com.example.pro_hive;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatFragment extends Fragment {

    private User selectedUser;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String currentUserID;
    private FirebaseUser user;
    private RecyclerView recyclerView;
//    private MessagesAdapter messagesAdapter;
    private ImageButton chat_send_button;
    private String groupId;
//    private List<Message> messages  = new ArrayList<>();;
    private String chatId;
    private EditText chat_input_edit_text;
    private OnFragmentInteractionListener listener;
    private RecyclerView recyclerView2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        currentUserID = user.getUid();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Chat");
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        chat_send_button = view.findViewById(R.id.chat_send_button);
        chat_input_edit_text = view.findViewById(R.id.chat_input_edit_text);
        recyclerView2 = view.findViewById(R.id.viewPostFragmentContainer);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));

        chat_send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!InternetCheck.internetConnectionAvailable(1000)) {
                    Toast.makeText(requireContext(),
                            "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                String chatText = chat_input_edit_text.getText().toString();
                if(chatText.isEmpty()){
                    Toast.makeText(getActivity(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, Object> message = new HashMap<>();
                message.put("text", chatText); // replace with the actual message text
                message.put("senderId", FirebaseAuth.getInstance().getCurrentUser().getUid()); // replace with the ID of the sender user
                message.put("timestamp", FieldValue.serverTimestamp()); // use Firestore server timestamp for consistency
                message.put("chatId", chatId); // add the chatId to the message data

                // Step 3: Add the message to the messages sub-collection under the given chatId
                db.collection("chats").document(chatId)
                        .collection("messages").add(message)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                chat_input_edit_text.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        });
        db = FirebaseFirestore.getInstance();


        return view;
    }

    public void updateChatId(String chatId) {
        this.chatId = chatId;

        CollectionReference messagesRef = db.collection("chats").document(chatId).collection("messages");
        Query query = messagesRef.orderBy("timestamp", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();


        ChatAdapter adapter = new ChatAdapter(options);
        recyclerView2.setAdapter(adapter);
        adapter.startListening();
    }
}