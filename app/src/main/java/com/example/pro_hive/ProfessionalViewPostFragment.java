package com.example.pro_hive;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfessionalViewPostFragment extends Fragment {

    private OnFragmentInteractionListener listener;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private FirebaseFirestore db;
    private Spinner field_of_interest;

    public ProfessionalViewPostFragment() {
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
        getActivity().setTitle("View Posts");
        if (!InternetCheck.internetConnectionAvailable(1000)) {
            Toast.makeText(requireContext(),
                    "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }
        View view = inflater.inflate(R.layout.fragment_professional_view_post, container, false);
        db = FirebaseFirestore.getInstance();
        field_of_interest = view.findViewById(R.id.field_of_interest_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.interestedFields, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        field_of_interest.setAdapter(adapter);
        spinnerChange();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query userPostsQuery = db.collectionGroup("user_posts");
        userPostsQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Post> postsList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Post post = documentSnapshot.toObject(Post.class);
                    post.setPostId(documentSnapshot.getId());
                    postsList.add(post);
                }
                // Pass the list of posts to the RecyclerView adapter to display them in cards
                recyclerView = view.findViewById(R.id.postsRecyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                postAdapter = new PostAdapter(postsList, listener, "Professional");
                recyclerView.setAdapter(postAdapter);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });





        return view;
    }

    public void spinnerChange(){
        field_of_interest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedField = parent.getItemAtPosition(position).toString();

                // Query the database for posts with the selected field
                Query userPostsQuery;
                if(position == 0){
                    userPostsQuery = db.collectionGroup("user_posts");
                }else{
                    userPostsQuery = db.collectionGroup("user_posts").whereEqualTo("fieldOfInterest", selectedField);;
                }

                userPostsQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Post> postsList = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Post post = documentSnapshot.toObject(Post.class);
                            post.setPostId(documentSnapshot.getId());
                            postsList.add(post);
                        }
                        // Pass the list of posts to the RecyclerView adapter to display them in cards
                        postAdapter.updatePosts(postsList);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });


                // Attach a listener to the query
//                userPostsQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                        if (error != null) {
//                            return;
//                        }
//
//                        List<Post> postsList = new ArrayList<>();
//                        for (QueryDocumentSnapshot documentSnapshot : value) {
//                            Post post = documentSnapshot.toObject(Post.class);
//                            post.setPostId(documentSnapshot.getId());
//                            postsList.add(post);
//                        }
//
//                        // Update the RecyclerView adapter with the new list of posts
//                        postAdapter.updatePosts(postsList);
//                    }
//                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
}