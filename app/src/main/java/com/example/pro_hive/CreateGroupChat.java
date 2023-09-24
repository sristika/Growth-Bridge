package com.example.pro_hive;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreateGroupChat extends Fragment {

    private Button create_group_button;
    private EditText group_name;
    private List<User> mUsers;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private GroupUserAdapter mUserAdapter;
    private OnFragmentInteractionListener listener;


    public CreateGroupChat() {
        // Required empty public constructor
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Create Group Chat");
        View view = inflater.inflate(R.layout.fragment_create_group_chat, container, false);
        mUsers = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get a reference to the "users" collection in Firestore
        CollectionReference usersRef = mFirestore.collection("users");

        // Use a query to retrieve all the users except for the current user
        Query query = usersRef.whereNotEqualTo("id", mAuth.getCurrentUser().getUid());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Convert each retrieved document to a User object
                    User user = document.toObject(User.class);
                    mUsers.add(user);
                }
                // Initialize the RecyclerView and the adapter
                initRecyclerView(view);
            } else {
                // Handle any errors
                
            }
        });





        return view;
    }

    private void initRecyclerView(View view) {
        RecyclerView userRecyclerView = view.findViewById(R.id.group_user_list);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUserAdapter = new GroupUserAdapter(getContext(), mUsers);
        userRecyclerView.setAdapter(mUserAdapter);
        String currentUserUid = mAuth.getCurrentUser().getUid();

        group_name = view.findViewById(R.id.group_name);


        create_group_button = view.findViewById(R.id.create_group_button);

        create_group_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!InternetCheck.internetConnectionAvailable(1000)) {
                    Toast.makeText(requireContext(),
                            "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                String group_name_str = group_name.getText().toString();

                if(group_name_str.isEmpty() || group_name_str == null){
                    Toast.makeText(getActivity().getApplicationContext(), "Group name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                GroupUserAdapter adapter = (GroupUserAdapter) userRecyclerView.getAdapter();

                // Get the list of selected users from the adapter
                List<String> selectedUsers = adapter.getSelectedUserIds();

                if(selectedUsers.size() <= 0){
                    Toast.makeText(getActivity().getApplicationContext(), "Please select at least one user", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectedUsers.add(currentUserUid);

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Create a new chat object with the necessary fields
                Map<String, Object> chat = new HashMap<>();
                chat.put("chatName", group_name_str);
                chat.put("isGroupChat", true);
                chat.put("members", selectedUsers);

                // Add the chat object to the "chats" collection and get the ID of the newly created document
                db.collection("chats").add(chat)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                String chatId = documentReference.getId();


                                // Update the chat object with the ID of the newly created document
                                chat.put("chatId", chatId);

                                // Update the document with the chat object
                                documentReference.set(chat)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                listener.navigateToFragment("conversationList");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });



            }
        });
    }
}