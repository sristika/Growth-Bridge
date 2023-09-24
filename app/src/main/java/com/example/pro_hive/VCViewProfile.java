package com.example.pro_hive;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class VCViewProfile extends Fragment {

    private User selectedUser;
    private ImageView profile_picture;
    private Button message;
    private OnFragmentInteractionListener listener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        getActivity().setTitle("Profile");
        View view = inflater.inflate(R.layout.fragment_v_c_view_profile, container, false);

        profile_picture = view.findViewById(R.id.profile_picture);
        message = view.findViewById(R.id.message_button);

        Bundle args = getArguments();
        if (args != null) {
            selectedUser = args.getParcelable("user");
        }

        TextView name = view.findViewById(R.id.name);
        TextView field = view.findViewById(R.id.interestedField);
        TextView country = view.findViewById(R.id.country_text);
        TextView jobTitle = view.findViewById(R.id.job_title);
        TextView phone = view.findViewById(R.id.phoneNum);
        TextView desc = view.findViewById(R.id.profile_text);
        String nameFull = selectedUser.getFirstName() + " " + selectedUser.getLastName();

        name.setText(nameFull);
        if(selectedUser.getField() != null){
            field.setText(selectedUser.getField());
        }

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.navigateToFragmentCreateChat("createChat", selectedUser.getId());
            }
        });

        country.setText(selectedUser.getCountry());
        if(selectedUser.getJobTitle() != null && !selectedUser.getJobTitle().isEmpty()){
            jobTitle.setText(selectedUser.getJobTitle());
        }
        phone.setText(selectedUser.getPhone());
        if(selectedUser.getDesc() != null && !selectedUser.getDesc().isEmpty()){
            desc.setText(selectedUser.getDesc());
        }

        if(selectedUser.getAvatarImage() != null && !selectedUser.getAvatarImage().isEmpty()){
            FirebaseFirestore.getInstance().collection("images").document(selectedUser.getAvatarImage())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String downloadUrl = (String) documentSnapshot.get("url");

                                // Load the image using the retrieved download URL
                                Glide.with(getContext()).load(downloadUrl).into(profile_picture);
                            } else {
                                // No matching document found
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle any errors
                        }
                    });
        }


        return view;
    }
}