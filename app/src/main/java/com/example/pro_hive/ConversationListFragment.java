package com.example.pro_hive;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ConversationListFragment extends Fragment {
    private OnFragmentInteractionListener listener;
    private List<Chat> chatList;

    private TextView createGroupChatTextView;


    public ConversationListFragment() {
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

    public static ConversationListFragment newInstance(String param1, String param2) {
        ConversationListFragment fragment = new ConversationListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Conversations List");
        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        createGroupChatTextView = view.findViewById(R.id.createGroupChatTextView);

        CollectionReference chatsRef = db.collection("chats");
        Query query = chatsRef.whereArrayContains("members", userId);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatList = new ArrayList<>();
                int totalTasks = task.getResult().size();
                AtomicInteger completedTasks = new AtomicInteger();
                for (QueryDocumentSnapshot document : task.getResult()) {

                    Chat chat = document.toObject(Chat.class);
                    Map<String, Object> chatData = document.getData();
                    if((boolean)chatData.get("isGroupChat") == true){
                        chat.setGroupChat(true);
                        chatList.add(chat);
                        completedTasks.getAndIncrement();
                        if (completedTasks.get() == totalTasks) {
                            // All queries have completed
                            ChatListAdapter adapter = new ChatListAdapter(chatList, listener);
                            RecyclerView recyclerView = view.findViewById(R.id.conversation_recycler_view);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                        }
                        continue;
                    }else {
                        CollectionReference messagesRef = db.collection("chats").document(document.toObject(Chat.class).getChatId()).collection("messages");
                        messagesRef.get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                if (!task2.getResult().isEmpty()) {
                                    // The messages collection does not exist
                                    // Add code here to create the collection

                                    chatList.add(chat);

//                                ChatListAdapter adapter = new ChatListAdapter(chatList, listener);
//                                RecyclerView recyclerView = view.findViewById(R.id.conversation_recycler_view);
//                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                                recyclerView.setAdapter(adapter);
                                }
                            } else {
                                // An error occurred while checking if the collection exists
                                // Handle the error here
                            }
                            completedTasks.getAndIncrement();
                            if (completedTasks.get() == totalTasks) {
                                // All queries have completed
                                ChatListAdapter adapter = new ChatListAdapter(chatList, listener);
                                RecyclerView recyclerView = view.findViewById(R.id.conversation_recycler_view);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView.setAdapter(adapter);
                            }
                        });
                    }



                }
                        // Use the chatList as needed

            } else {
                        // Handle error

            }
        });

        createGroupChatTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.navigateToFragment("createGroupChat");
            }
        });
        return view;
    }
}