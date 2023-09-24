package com.example.pro_hive;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VCHomeActivity extends AppCompatActivity implements OnFragmentInteractionListener{

    private TextView logout;
    private FirebaseStorage storage;


    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vchome);
        storage = FirebaseStorage.getInstance();

        logout = findViewById(R.id.logOut);
        bottomNavigationView = findViewById(R.id.vc_bottom_navigation);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(VCHomeActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.vc_home:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.vc_fragment_container,new HomePageVCs())
                                .addToBackStack(null)
                                .commit();
                        return true;
                    case R.id.vc_chat:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.vc_fragment_container,new ConversationListFragment())
                                .addToBackStack(null)
                                .commit();
                        return true;
                    case R.id.vc_view_post:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.vc_fragment_container,new VCViewPostFragment())
                                .addToBackStack(null)
                                .commit();
                        return true;
                    case R.id.vc_create_post:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.vc_fragment_container,new VCRequirementsPostFragment())
                                .addToBackStack(null)
                                .commit();
                        return true;
                    case R.id.vc_profile:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.vc_fragment_container,new VCEditProfile())
                                .addToBackStack(null)
                                .commit();
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public void navigateToFragment(String fragmentName) {
        if(fragmentName.equals("edit")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.vc_fragment_container,new VCRequirementsEditFragment())
                    .addToBackStack(null)
                    .commit();
        }else if(fragmentName.equals("posts")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.vc_fragment_container,new VCViewPostFragment())
                    .addToBackStack(null)
                    .commit();
        }else if(fragmentName.equals("createGroupChat")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.vc_fragment_container,new CreateGroupChat())
                    .addToBackStack(null)
                    .commit();
        }else if(fragmentName.equals("conversationList")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.vc_fragment_container, new ConversationListFragment())
                    .addToBackStack(null)
                    .commit();
        }else if (fragmentName.equals("cameraController")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.vc_fragment_container, new FragmentCameraController(), "cameraFragment")
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void navigateToFragmentPostData(String fragmentName, Post post) {
        if (fragmentName.equals("edit")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.vc_fragment_container, new VCRequirementsEditFragment())
                    .addToBackStack(null)
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
            VCRequirementsEditFragment fragment = (VCRequirementsEditFragment) getSupportFragmentManager().findFragmentById(R.id.vc_fragment_container);
            fragment.updatePost(post);
        }
    }

    @Override
    public void navigateToFragmentChatData(String fragmentName, String chatId) {
        if (fragmentName.equals("chat")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.vc_fragment_container, new ChatFragment())
                    .addToBackStack(null)
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
            ChatFragment fragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.vc_fragment_container);
            fragment.updateChatId(chatId);
        }
    }

    @Override
    public void navigateToFragmentCreateChat(String fragmentName, String userId) {
        if(fragmentName.equals("createChat")){
            // Assume userId1 and userId2 are the two user IDs provided
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            CollectionReference chatsRef = FirebaseFirestore.getInstance().collection("chats");
            Query query = chatsRef.whereEqualTo("isGroupChat", false)
                    .whereArrayContains("members", currentUserId);

            final boolean[] chatExists = {false};

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // A chat with exactly 2 users exists
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            List<String> members = (List<String>) document.get("members");
                            if (members != null && members.size() == 2 && members.contains(userId)) {
                                // A chat with exactly 2 users exists and the current user is a member
                                chatExists[0] = true;
                                String chatId = document.getId();
                                Map<String, Object> chatData = document.getData();
                                // Use chatId and chatData as needed
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.vc_fragment_container, new ChatFragment())
                                        .addToBackStack(null)
                                        .commit();
                                getSupportFragmentManager().executePendingTransactions();
                                ChatFragment fragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.vc_fragment_container);
                                fragment.updateChatId(chatId);
                                break; // break out of the loop once a suitable chat group is found
                            }
                        }
                    }

                    if(!chatExists[0]){
                        // A chat with exactly 2 users does not exist, create a new document
                        DocumentReference newChatRef = chatsRef.document();
                        String newChatId = newChatRef.getId();
                        Map<String, Object> newChatData = new HashMap<>();
                        newChatData.put("chatName", "");
                        newChatData.put("isGroupChat", false);
                        newChatData.put("members", Arrays.asList(userId, currentUserId));
                        newChatData.put("chatId", newChatId);

                        newChatRef.set(newChatData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Use newChatId and newChatData as needed
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.vc_fragment_container, new ChatFragment())
                                            .addToBackStack(null)
                                            .commit();
                                    getSupportFragmentManager().executePendingTransactions();
                                    ChatFragment fragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.vc_fragment_container);
                                    fragment.updateChatId(newChatId);
                                } else {

                                }
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    @Override
    public void onTakePhoto(Uri imageUri) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.vc_fragment_container,FragmentDisplayImage.newInstance(imageUri),"displayFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onOpenGalleryPressed() {
        openGallery();
    }

    @Override
    public void onRetakePressed() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.vc_fragment_container, new FragmentCameraController(), "cameraFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onUploadButtonPressed(Uri imageUri, ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);

        StorageReference storageReference = storage.getReference().child("images/"+imageUri.getLastPathSegment());
        UploadTask uploadImage = storageReference.putFile(imageUri);
        String uuid = UUID.randomUUID().toString();

        uploadImage.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VCHomeActivity.this, "Upload Failed! Try again!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Get the download URL for the uploaded image
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Create a new document in the "images" collection with the download URL as data
                                Map<String, Object> data = new HashMap<>();
                                data.put("url", uri.toString());
                                FirebaseFirestore.getInstance().collection("images")
                                        .document(uuid)
                                        .set(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.vc_fragment_container,new VCEditProfile())
                                                        .addToBackStack(null)
                                                        .commit();
                                                getSupportFragmentManager().executePendingTransactions();
                                                VCEditProfile fragment = (VCEditProfile) getSupportFragmentManager().findFragmentById(R.id.vc_fragment_container);
                                                fragment.updateAvatarImage(uuid);
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(VCHomeActivity.this, "Upload Failed! Try again!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressBar.setProgress((int) progress);
                    }
                });
    }

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==RESULT_OK){
                        Intent data = result.getData();
                        Uri selectedImageUri = data.getData();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.vc_fragment_container,FragmentDisplayImage.newInstance(selectedImageUri),"displayFragment")
                                .addToBackStack(null)
                                .commit();
                    }
                }
            }
    );

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        galleryLauncher.launch(intent);
    }
}
