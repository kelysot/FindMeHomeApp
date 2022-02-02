package com.example.findmehomeapp.ui.CreatePost;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.Post;
import com.example.findmehomeapp.R;
import com.example.findmehomeapp.ui.home.HomeFragmentDirections;
import com.google.firebase.auth.FirebaseAuth;

public class CreatePostFragment extends Fragment {

    FirebaseAuth firebaseAuth;

    EditText petTextEt;
    ImageView pictureIv;

    String petText;
    String type;
    String age;
    String size;
    String gender;
    String location;

    EditText ageEt;
    Spinner typeSpinner;
    Spinner sizeSpinner;
    Spinner genderSpinner;
    Spinner locationSpinner;

    Button creteBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        // Type
        typeSpinner = view.findViewById(R.id.create_post_type_spinner);
        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this.getContext(),
                R.array.types, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapterType);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Size
        sizeSpinner = view.findViewById(R.id.create_post_size_spinner);
        ArrayAdapter<CharSequence> adapterSize = ArrayAdapter.createFromResource(this.getContext(),
                R.array.sizes, android.R.layout.simple_spinner_item);
        adapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(adapterSize);
        sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                size = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Gender
        genderSpinner = view.findViewById(R.id.create_post_gender_spinner);
        ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(this.getContext(),
                R.array.gender, android.R.layout.simple_spinner_item);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapterGender);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Location
        locationSpinner = view.findViewById(R.id.create_post_location_spinner);
        ArrayAdapter<CharSequence> adapterLocation = ArrayAdapter.createFromResource(this.getContext(),
                R.array.location, android.R.layout.simple_spinner_item);
        adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapterLocation);
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                location = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // create post btn
        creteBtn = view.findViewById(R.id.create_post_btn);
        creteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pet text
                petTextEt = view.findViewById(R.id.create_post_text);
                petText = petTextEt.getText().toString();

                // Age
                ageEt = view.findViewById(R.id.create_post_age_et);
                age = ageEt.getText().toString();

                //TODO: Validate user input
                savePost();
            }
        });

        return view;
    }

    private void savePost(){

        String userId = firebaseAuth.getCurrentUser().getUid();
        Post post = new Post(userId, petText, "s", type, age, size, gender, location);

        Model.instance.addPost(post, () -> {
            NavHostFragment.findNavController(this).navigateUp();
        });
    }
}