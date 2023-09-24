package com.example.pro_hive;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class HomePageProfessionals extends Fragment{

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private List<User> users = new ArrayList<>();
    private List<User> searchedUsers = new ArrayList<>();
    private List<User> selectedUsers;

    private Spinner country;
    private Spinner field;
    private Button searchBtn;
    private EditText searchField;
    private CheckBox isVC;

    private String countryData = "";
    private String fieldData = "";

    private boolean VC = false;
    private OnFragmentInteractionListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
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
        getActivity().setTitle("Home");
        View view = inflater.inflate(R.layout.fragment_home_page_professionals, container, false);


            recyclerView = view.findViewById(R.id.homeProfRecycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            userAdapter = new UserAdapter(1, recyclerView, listener);
            recyclerView.setAdapter(userAdapter);

            field = view.findViewById(R.id.required_field_spinner);
            country = view.findViewById(R.id.country_spinner);
            searchBtn = view.findViewById(R.id.searchbutton);
            searchField = view.findViewById(R.id.editTextSearch);
            isVC = view.findViewById(R.id.vc_checkbox);

        if(InternetCheck.internetConnectionAvailable(1000)) {
            getUsersFromFirebase();

            isVC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VC = isVC.isChecked();
                    getSelectedUsers();
                }
            });

            field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    if (!selectedItem.equals("Select field")) {
                        fieldData = selectedItem;
                        getSelectedUsers();
                    } else {
                        fieldData = "";
                        getSelectedUsers();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    if (!selectedItem.equals("Select country")) {
                        countryData = selectedItem;
                        getSelectedUsers();
                    } else {
                        countryData = "";
                        getSelectedUsers();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            searchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String searchedWord = searchField.getText().toString();
                    if (!searchedWord.isEmpty()) {
                        searchedUsers.clear();

                        for (User u : users) {
                            if ((u.getFirstName() + u.getLastName()).toLowerCase().contains(searchedWord.toLowerCase())) {
                                searchedUsers.add(u);
                            }
                        }
                    } else {
                        searchedUsers = new ArrayList<>(users);
                    }
                    userAdapter.setUsers(searchedUsers);
                    userAdapter.notifyDataSetChanged();
                    field.setSelection(0);
                    country.setSelection(0);
                }
            });
        }else{
            Toast.makeText(requireContext(),
                    "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void getSelectedUsers() {
        List<User> tempArr = new ArrayList<>();
        if(!countryData.isEmpty()){
            for(User u: searchedUsers){
                if(u.getCountry().equals(countryData)){
                    tempArr.add(u);
                }
            }
        }else{
            tempArr = new ArrayList<>(searchedUsers);
        }
        if(!fieldData.isEmpty()){
            tempArr.removeIf(u -> u.getField() != null && !u.getField().equals(fieldData));
        }
        if(VC){
            tempArr.removeIf(u -> !u.isVc());
        }
        selectedUsers = tempArr;
        userAdapter.setUsers(selectedUsers);
        userAdapter.notifyDataSetChanged();
    }

    private void getUsersFromFirebase() {
        List<User> users = new ArrayList<>();
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> usersFromFb = task.getResult().getDocuments();

                for (DocumentSnapshot userDoc : usersFromFb) {
                    User user = userDoc.toObject(User.class);
                    user.setId(userDoc.getId());
                    if(!userDoc.getId().equals(auth.getUid())){
                        users.add(user);
                    }
                }
                this.users = users;
                this.selectedUsers = new ArrayList<>(users);
                this.searchedUsers = new ArrayList<>(users);
                userAdapter.setUsers(users);
                userAdapter.notifyDataSetChanged();
            }
        });
    }
}