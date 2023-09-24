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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VCRequirementsEditFragment extends Fragment {

    private EditText titleEditText;
    private Spinner requiredFieldSpinner;
    private EditText budgetEditText;
    private EditText contactInfoEditText;
    private EditText projectRequirementsEditText;
    private Button editButton;
    private OnFragmentInteractionListener listener;

    Post postObj;

    public VCRequirementsEditFragment() {
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
        getActivity().setTitle("Edit Post");
        View view = inflater.inflate(R.layout.fragment_v_c_requirements_edit, container, false);
        titleEditText = view.findViewById(R.id.title_edittext);
        requiredFieldSpinner = view.findViewById(R.id.required_field_spinner);
        budgetEditText = view.findViewById(R.id.budget_edittext);
        contactInfoEditText = view.findViewById(R.id.contact_info_edittext);
        projectRequirementsEditText = view.findViewById(R.id.project_requirements_edittext);
        editButton = view.findViewById(R.id.update_button);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!InternetCheck.internetConnectionAvailable(1000)) {
                    Toast.makeText(requireContext(),
                            "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference postRef = db.collection("posts").document(userId).collection("user_posts").document(postObj.getPostId());
                Map<String, Object> updates = new HashMap<>();

                if(titleEditText.getText().toString().isEmpty() || requiredFieldSpinner.getSelectedItem().toString().isEmpty()
                || budgetEditText.getText().toString().isEmpty() || contactInfoEditText.getText().toString().isEmpty()
                || projectRequirementsEditText.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "All the fields are mandatory.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!budgetEditText.getText().toString().matches("\\d+(\\.\\d+)?")) {
                    Toast.makeText(getActivity(), "Error: Please enter a valid number", Toast.LENGTH_SHORT).show();
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(contactInfoEditText.getText().toString()).matches()) {
                    Toast.makeText(getActivity(), "Invalid Email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                updates.put("title", titleEditText.getText().toString());
                updates.put("fieldOfInterest", requiredFieldSpinner.getSelectedItem().toString());
                updates.put("budget", Float.parseFloat(budgetEditText.getText().toString()));
                updates.put("contactInfo", contactInfoEditText.getText().toString());
                updates.put("projectRequirements", projectRequirementsEditText.getText().toString());
                postRef.update(updates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Post updated successfully", Toast.LENGTH_SHORT).show();
                                listener.navigateToFragment("posts");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });

        return view;
    }

    public void updatePost(Post post) {
        postObj = post;
        titleEditText.setText(postObj.getTitle());
        budgetEditText.setText(String.format("%.0f", postObj.getBudget()));
        contactInfoEditText.setText(post.getContactInfo());
        projectRequirementsEditText.setText(post.getProjectRequirements());
        String[] interestedFields = getResources().getStringArray(R.array.interestedFields);
        int position = Arrays.asList(interestedFields).indexOf(post.getFieldOfInterest());
        requiredFieldSpinner.setSelection(position);
    }
}