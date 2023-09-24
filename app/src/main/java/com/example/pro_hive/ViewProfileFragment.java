package com.example.pro_hive;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class ViewProfileFragment extends Fragment {
    private User selectedUser;
    private OnFragmentInteractionListener listener;

    private Button message_button;

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
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        Bundle args = getArguments();
        if (args != null) {
            selectedUser = args.getParcelable("user");
        }

        message_button = view.findViewById(R.id.message_button);

        TextView name = view.findViewById(R.id.name);
        TextView field = view.findViewById(R.id.job_field);
        TextView country = view.findViewById(R.id.country_text);
        TextView jobTitle = view.findViewById(R.id.job_title);
        TextView phone = view.findViewById(R.id.phoneNum);
        TextView desc = view.findViewById(R.id.profile_text);
        TextView skills = view.findViewById(R.id.skills_text);
        String nameFull = selectedUser.getFirstName() + " " + selectedUser.getLastName();

        message_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.navigateToFragmentCreateChat("createChat", selectedUser.getId());
            }
        });

        name.setText(nameFull);
        if(selectedUser.getField() != null){
            field.setText(selectedUser.getField());
        }

        country.setText(selectedUser.getCountry());
        if(selectedUser.getJobTitle() != null && !selectedUser.getJobTitle().isEmpty()){
            jobTitle.setText(selectedUser.getJobTitle());
        }
        phone.setText(selectedUser.getPhone());
        if(selectedUser.getDesc() != null && !selectedUser.getDesc().isEmpty()){
            desc.setText(selectedUser.getDesc());
        }
        if(selectedUser.getSkills() != null && !selectedUser.getSkills().isEmpty()){
            String s = "Skills: " + selectedUser.getSkills();
            skills.setText(s);
        }
        return view;
    }
}