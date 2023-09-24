package com.example.pro_hive;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText emailAdd;
    private EditText password;
    private TextView goToRegister;
    private Button login;
    private TextView reset;

    private OnFragmentInteractionListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
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
        getActivity().setTitle("Login");
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        goToRegister = view.findViewById(R.id.register_textview);
        login = view.findViewById(R.id.login_button);
        emailAdd = view.findViewById(R.id.email_edittext);
        password = view.findViewById(R.id.password_edittext);
        reset = view.findViewById(R.id.forgot);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.navigateToFragment("reset password");
            }
        });

        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.navigateToFragment("register");
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isVc;

                if (InternetCheck.internetConnectionAvailable(1000)) {
                mAuth.signInWithEmailAndPassword((emailAdd.getText()).toString(), (password.getText()).toString())
                        .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    db.collection("users").document(mAuth.getUid()).get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists()) {
                                                    User user = documentSnapshot.toObject(User.class);
                                                    if(user.isVc()){
                                                        Intent intent = new Intent(getActivity(), VCHomeActivity.class);
                                                        startActivity(intent);
                                                    }else{
                                                        Intent intent = new Intent(getActivity(), ProfessionalHomeActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                    });
                                } else {
                                    Toast.makeText(requireContext(), "Authentication failed. Enter valid email and password.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                } else{
                    Toast.makeText(requireContext(),
                            "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                }
            }

        });

        return view;
    }

    public void setOnFragmentInteractionListener(OnFragmentInteractionListener listener) {
        this.listener = listener;
    }

}