package com.example.pro_hive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> mPostsList;
    private OnFragmentInteractionListener listener;
    private String userType;

    public PostAdapter(List<Post> postsList, OnFragmentInteractionListener listener, String user) {
        mPostsList = postsList;
        this.listener = listener;
        this.userType = user;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vc_item_post, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = mPostsList.get(position);
        holder.titleTextView.setText(post.getTitle());
        holder.requiredFieldTextView.setText("Field of interest: " + post.getFieldOfInterest());
        holder.budgetTextView.setText("Budget: " + String.format("%.0f", post.getBudget()));
        holder.contactInfoTextView.setText("Contact Information: " + post.getContactInfo());
        holder.projectRequirementsTextView.setText("Project Requirements: " + post.getProjectRequirements());


        if (userType.equals("Professional")) {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        } else {

            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Add your edit button functionality here
                    listener.navigateToFragmentPostData("edit", post);
                }
            });

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    // Check if position is valid
                    if (position != RecyclerView.NO_POSITION) {
                        Post post = mPostsList.get(position);
                        // Delete the post from the database
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        db.collection("posts").document(userId).collection("user_posts").document(post.getPostId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Post deleted successfully
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error occurred while deleting the post
                                    }
                                });
                        // Remove the post from the list
                        mPostsList.remove(position);
                        // Notify the adapter that the item has been removed
                        notifyItemRemoved(position);
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mPostsList.size();
    }

    public void updatePosts(List<Post> postsList) {
        this.mPostsList = postsList;
        notifyDataSetChanged();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView requiredFieldTextView;
        public TextView budgetTextView;
        public TextView contactInfoTextView;
        public TextView projectRequirementsTextView;
        public Button editButton;
        public Button deleteButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_textview);
            requiredFieldTextView = itemView.findViewById(R.id.required_field_textview);
            budgetTextView = itemView.findViewById(R.id.budget_textview);
            contactInfoTextView = itemView.findViewById(R.id.contact_info_textview);
            projectRequirementsTextView = itemView.findViewById(R.id.project_requirements_textview);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
