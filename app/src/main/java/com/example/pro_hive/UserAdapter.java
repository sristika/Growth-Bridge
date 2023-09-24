package com.example.pro_hive;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private List<User> users;

    public void setUsers(List<User> users) {
        this.users = users;
    }

    private FirebaseAuth auth;
    private String currentUserId;
    private RecyclerView recyclerView;
    private int userType;
    private OnFragmentInteractionListener listener;
    private FirebaseFirestore db;
    private User curr;


    public UserAdapter(int userType, RecyclerView recyclerView, OnFragmentInteractionListener listener) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        this.currentUserId = user.getUid();
        this.recyclerView = recyclerView;
        this.userType = userType;
        this.listener = listener;

        CollectionReference usersRef = db.collection("users");
        DocumentReference userDocRef = usersRef.document(auth.getUid());

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            curr = documentSnapshot.toObject(User.class);
        });

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vc_home_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.userName.setText(user.getFirstName() + " " + user.getLastName());
        holder.userAddress.setText(user.getCountry());
        holder.job_title.setText(user.getJobTitle());
        if(user.getField() != null && !user.getField().equals(null)){
            holder.field.setText(user.getField());
        }
        holder.message.setOnClickListener(v -> {
             // Create a new MessageFragment and pass the User object as an argument
           listener.navigateToFragmentCreateChat("createChat", user.getId());
        });

        holder.viewProfile.setOnClickListener(v -> {
            // Create a new MessageFragment and pass the User object as an argument
            VCViewProfile profileFragmentVC = new VCViewProfile();
            ViewProfileFragment profileFragmentProf = new ViewProfileFragment();
            if(user.isVc()){
                profileFragmentVC = new VCViewProfile();
                Bundle args = new Bundle();
                args.putParcelable("user", user);
                profileFragmentVC.setArguments(args);
            }else{
                profileFragmentProf = new ViewProfileFragment();
                Bundle args = new Bundle();
                args.putParcelable("user", user);
                profileFragmentProf.setArguments(args);
            }

            // Replace the current fragment container with the new MessageFragment
            FragmentManager fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if(curr.isVc()){
                if(user.isVc()){
                    fragmentTransaction.replace(R.id.vc_fragment_container, profileFragmentVC);
                }else{
                    fragmentTransaction.replace(R.id.vc_fragment_container, profileFragmentProf);
                }
            }else{
                if(user.isVc()){
                    fragmentTransaction.replace(R.id.prof_fragmentContainerView, profileFragmentVC);
                }else{
                    fragmentTransaction.replace(R.id.prof_fragmentContainerView, profileFragmentProf);
                }
            }

            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        private TextView userAddress;
        private TextView job_title;
        private TextView field;

        private Button message;
        private Button viewProfile;


        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.nameTextView);
            userAddress = itemView.findViewById(R.id.addressTextView);
            job_title = itemView.findViewById(R.id.jobTitleTextView);
            field = itemView.findViewById(R.id.jobFieldTextView);
            message = itemView.findViewById(R.id.messageButton);
            viewProfile = itemView.findViewById(R.id.viewProfileButton);
        }
    }
}
