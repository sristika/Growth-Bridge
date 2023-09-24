package com.example.pro_hive;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Login/Register");

        Login loginFragment = new Login();
        loginFragment.setOnFragmentInteractionListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,new Login())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void navigateToFragment(String fragmentName) {
        if(fragmentName.equals("register")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,new Register())
                    .addToBackStack(null)
                    .commit();
        }
        else if(fragmentName.equals("Login")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,new Login())
                    .addToBackStack(null)
                    .commit();
        }
        else if(fragmentName.equals("reset password")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,new ForgotPasswordFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void navigateToFragmentPostData(String fragmentName, Post post) {

    }

    @Override
    public void navigateToFragmentChatData(String fragmentName, String chatId) {

    }

    @Override
    public void navigateToFragmentCreateChat(String fragmentName, String userId) {

    }

    @Override
    public void onTakePhoto(Uri imageUri) {

    }

    @Override
    public void onOpenGalleryPressed() {

    }

    @Override
    public void onRetakePressed() {

    }

    @Override
    public void onUploadButtonPressed(Uri imageUri, ProgressBar progressBar) {

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(userId);
            Toast.makeText(getApplicationContext(), "Automatically Signing In ...", Toast.LENGTH_SHORT).show();


            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        // use the user object
                        if(user.isVc()){
                            Intent intent = new Intent(MainActivity.this, VCHomeActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent = new Intent(MainActivity.this, ProfessionalHomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // handle any errors
                }
            });
        }

    }
}