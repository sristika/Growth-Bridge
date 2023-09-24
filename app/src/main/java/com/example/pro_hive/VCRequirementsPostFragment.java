package com.example.pro_hive;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class VCRequirementsPostFragment extends Fragment {
    private EditText titleEditText;
    private Spinner requiredFieldSpinner;
    private EditText budgetEditText;
    private EditText contactInfoEditText;
    private EditText projectRequirementsEditText;
    private Button submitButton;


    public VCRequirementsPostFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Create Post");
        View view = inflater.inflate(R.layout.fragment_v_c_requirements_post, container, false);
        // Initialize the views
        titleEditText = view.findViewById(R.id.title_edittext);
        requiredFieldSpinner = view.findViewById(R.id.required_field_spinner);
        budgetEditText = view.findViewById(R.id.budget_edittext);
        contactInfoEditText = view.findViewById(R.id.contact_info_edittext);
        projectRequirementsEditText = view.findViewById(R.id.project_requirements_edittext);

        // Set the click listener for the submit button
        submitButton = view.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!InternetCheck.internetConnectionAvailable(1000)) {
                    Toast.makeText(requireContext(),
                            "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                onSubmitClicked();
            }
        });
        return view;
    }

    private void onSubmitClicked() {
        // Get input values from fields
        String title = titleEditText.getText().toString();
        String fieldOfInterest = requiredFieldSpinner.getSelectedItem().toString();

        String contactInfo = contactInfoEditText.getText().toString();
        String projectRequirements = projectRequirementsEditText.getText().toString();

        if(title.isEmpty() || fieldOfInterest.isEmpty() || budgetEditText.getText().toString().isEmpty()
                || contactInfo.isEmpty() || projectRequirements.isEmpty()){
            Toast.makeText(getActivity(), "All the fields are mandatory.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!budgetEditText.getText().toString().matches("\\d+(\\.\\d+)?")) {
            Toast.makeText(getActivity(), "Error: Please enter a valid number", Toast.LENGTH_SHORT).show();
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(contactInfo).matches()) {
            Toast.makeText(getActivity(), "Invalid Email!", Toast.LENGTH_SHORT).show();
            return;
        }

            float budget = Float.parseFloat(budgetEditText.getText().toString());

        // Get user ID (replace with your own way of getting the user ID)
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();



        // Create a new document with the user's ID as the document ID
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("posts").document(userId).collection("user_posts").document();
        Post post = new Post(title, fieldOfInterest, budget, contactInfo, projectRequirements);
        docRef.set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Clear fields
                        titleEditText.setText("");
                        requiredFieldSpinner.setSelection(0);
                        budgetEditText.setText("");
                        contactInfoEditText.setText("");
                        projectRequirementsEditText.setText("");

                        // Display toast message
                        Toast.makeText(getActivity(), "Post added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to add post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}