package com.example.pro_hive;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends Fragment {

    private OnFragmentInteractionListener listener;
    private TextView goToLogin;

    private EditText emailAdd;
    private EditText password;
    private EditText phone;
    private EditText fName;
    private EditText lName;
    private EditText confirmPass;
    private EditText jobTitle;
    private Spinner country;
    private Spinner field;
    private CheckBox vc;

    private Button register;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


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
        getActivity().setTitle("Register");
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        goToLogin = view.findViewById(R.id.login_textview);
        register = view.findViewById(R.id.submit_button);
        emailAdd = view.findViewById(R.id.email_edittext);
        password = view.findViewById(R.id.password_edittext);
        phone = view.findViewById(R.id.phone_edittext);
        fName = view.findViewById(R.id.first_name_edittext);
        lName = view.findViewById(R.id.last_name_edittext);
        confirmPass = view.findViewById(R.id.confirm_password_edittext);
        jobTitle = view.findViewById(R.id.jobTitle_editText);
        field = view.findViewById(R.id.required_field_spinner);
        final String[] fieldData = new String[1];
        final String[] countryData = new String[1];
        country = view.findViewById(R.id.country_spinner);
        vc = view.findViewById(R.id.vc_checkbox);

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.navigateToFragment("Login");
            }
        });

        field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                fieldData[0] = selectedItem;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fieldData[0] = "N/A";
            }
        });

        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                countryData[0] = selectedItem;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                countryData[0] = "N/A";
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("firstName", fName.getText().toString());
                userMap.put("lastName", lName.getText().toString());
                userMap.put("email", (emailAdd.getText()).toString());
                userMap.put("phone", phone.getText().toString());
                userMap.put("jobTitle", jobTitle.getText().toString());
                userMap.put("vc", vc.isChecked());
                if (vc.isChecked()) {
                    userMap.put("jobTitle", "Venture Capitalist");
                }
                if (!fieldData[0].equals("N/A") && !fieldData[0].equals("Select field")) {
                    userMap.put("field", fieldData[0]);
                }
                if (!countryData[0].equals("N/A") && !countryData[0].equals("Select country")) {
                    userMap.put("country", countryData[0]);
                }

                if (InternetCheck.internetConnectionAvailable(1000)) {
                    if (isValid(userMap, (password.getText()).toString(), (confirmPass.getText()).toString())) {

                        mAuth = FirebaseAuth.getInstance();


                        mAuth.createUserWithEmailAndPassword(emailAdd.getText().toString(), password.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // User creation success
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            String userId = user.getUid(); // Get the user ID
                                            userMap.put("id", userId);

                                            db.collection("users")
                                                    .document(userId) // Set the document ID as user ID
                                                    .set(userMap);

                                            showToast("User created successfully.");
                                            listener.navigateToFragment("Login");
                                        } else {
                                            showToast("User creation failed.");
                                        }
                                    }
                                });
                    } else {
                        showToast("Invalid inputs. Email format should be: abc@test.com. Password: Atleast 8 characters.");
                    }
                } else{
                    Toast.makeText(requireContext(),
                            "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                }
            }

        });

        return view;
    }

    private boolean isValid(Map<String, Object> u, String pw, String cpw) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Boolean email = u.get("email").toString().matches(emailRegex);
        Boolean firstName = !u.get("firstName").equals("");
        Boolean lastName = !u.get("lastName").equals("");
        Boolean phone = !u.get("phone").equals("");
        Boolean password = pw.length() >= 8 && pw.equals(cpw);
        Boolean country = false;
        if(u.containsKey("country"))
            country = true;



        if(email && firstName && lastName && phone && country && password)
            return true;
        else
            return false;
    }

    private void showToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}