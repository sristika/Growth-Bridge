package com.example.pro_hive;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class EditProfileFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EditText firstName;
    private EditText lastName;
    private EditText phone;
    private EditText desc;
    private Spinner field;
    private Spinner country;
    private EditText title;
    private Button editButton;
    private Resources res;
    private String[] countries;
    private String[] fields;

    private int fieldData;
    private int countryData;
    private EditText skills;
    private String avatarImageId = "";
    private String avatar = "";
    private ImageView profile_picture_edit;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private Boolean cameraAllowed;
    private Boolean readAllowed;
    private Boolean writeAllowed;
    private OnFragmentInteractionListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
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

    private void requestPermissions() {
        String[] permissions = new String[]{android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissionLauncher.launch(permissions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Edit Profile");
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        res = getResources();
        fields  = res.getStringArray(R.array.interestedFields);
        countries  = res.getStringArray(R.array.country_names);
        firstName = view.findViewById(R.id.name_edit);
        lastName = view.findViewById(R.id.lastName_edit);
        phone = view.findViewById(R.id.editTextPhone);
        desc = view.findViewById(R.id.descriptionEdit);
        field = view.findViewById(R.id.required_field_spinner);
        country = view.findViewById(R.id.country_spinner);
        editButton = view.findViewById(R.id.editButton);
        title = view.findViewById(R.id.title_edit);
        skills = view.findViewById(R.id.edit_skills);

        profile_picture_edit = view.findViewById(R.id.profile_picture_edit);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                        boolean allPermissionsGranted = true;
                        for (Boolean value : result.values()) {
                            allPermissionsGranted &= value;
                        }

                        if (allPermissionsGranted) {
                            cameraAllowed = true;
                            readAllowed = true;
                            writeAllowed = true;
                            Toast.makeText(getContext(), "All permissions granted!", Toast.LENGTH_SHORT).show();
                            listener.navigateToFragment("cameraController");
                        } else {
                            cameraAllowed = false;
                            readAllowed = false;
                            writeAllowed = false;
                            Toast.makeText(getContext(), "Permissions not granted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Boolean cameraAllowed = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        Boolean readAllowed = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        Boolean writeAllowed = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        profile_picture_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cameraAllowed && readAllowed && writeAllowed) {
                    Toast.makeText(getContext(), "All permissions granted!", Toast.LENGTH_SHORT).show();
                    listener.navigateToFragment("cameraController");
                } else {
                    requestPermissions();
                }
            }
        });


        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        firstName.setText(user.getFirstName() == null ? "": user.getFirstName());
                        lastName.setText(user.getLastName() == null ? "": user.getLastName());
                        phone.setText(user.getPhone() == null ? "" : user.getPhone());
                        desc.setText(user.getDesc() == null ? "" : user.getDesc());
                        title.setText(user.getJobTitle() == null ? "" : user.getJobTitle());
                        skills.setText(user.getSkills() == null ? "" : user.getSkills());

                        if(user.getAvatarImage() != null && !user.getAvatarImage().isEmpty()){
                            FirebaseFirestore.getInstance().collection("images").document(user.getAvatarImage())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                String downloadUrl = (String) documentSnapshot.get("url");

                                                // Load the image using the retrieved download URL
                                                Glide.with(getContext()).load(downloadUrl).into(profile_picture_edit);
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

                        if(user.getField() != null){
                            field.setSelection(findIndex(fields, user.getField()));
                        }
                        if(user.getCountry() != null){
                            country.setSelection(findIndex(countries, user.getCountry()));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                });

        field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fieldData = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fieldData = 0;
            }
        });

        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryData = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                countryData = 0;
            }
        });

        editButton.setOnClickListener(v -> {
            String newFirstName = firstName.getText().toString();
            String newLastName = lastName.getText().toString();
            String newPhone = phone.getText().toString();
            String newDesc = desc.getText().toString();
            String newTitle = title.getText().toString();
            String newSkills = skills.getText().toString();

            if(InternetCheck.internetConnectionAvailable(1000)) {

                if(!newFirstName.isEmpty() && !newLastName.isEmpty() && !newPhone.isEmpty() && countryData!=0){
                db.collection("users").document(userId).update(
                                "firstName", newFirstName,
                                "lastName", newLastName,
                                "country", countries[countryData],
                                "desc", newDesc,
                                "jobTitle", newTitle,
                                "skills", newSkills,
                                "avatarImage", avatarImageId
                        )
                        .addOnSuccessListener(aVoid -> {
                            showToast("Profile updated!");
                        })
                        .addOnFailureListener(e -> {
                        });
                if(fieldData != 0){
                    db.collection("users").document(userId).update(
                            "field", fields[fieldData]
                    );
                }
            }else{
                showToast("Invalid inputs. All fields except \"Field\", \"Description\" & \"Job title\" are mandatory. ");
            }
        }
        else{
            Toast.makeText(requireContext(),
                    "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }
        });

        return view;
    }

    public static int findIndex(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    private void showToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void updateAvatarImage(String uuid) {
        avatar = uuid;
        avatarImageId = uuid;
        FirebaseFirestore.getInstance().collection("images").document(avatar)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String downloadUrl = (String) documentSnapshot.get("url");

                            // Load the image using the retrieved download URL
                            Glide.with(getContext()).load(downloadUrl).into(profile_picture_edit);

                            // Update the avatar variable with the new value
                            avatar = documentSnapshot.getId();
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
}