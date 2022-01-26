package com.example.findmehomeapp.ui.EditPost;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.Post;
import com.example.findmehomeapp.Model.User;
import com.example.findmehomeapp.R;
import com.example.findmehomeapp.ui.CreatePost.CreatePostFragmentDirections;
import com.example.findmehomeapp.ui.Post.PostFragmentArgs;
import com.example.findmehomeapp.ui.Post.PostFragmentDirections;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class EditPostFragment extends Fragment {
    Post updatedPost;
    TextView petTextTv;
    ImageView petImage;
    Spinner typeSpinner;
    EditText ageEt;
    Spinner sizeSpinner;
    Spinner genderSpinner;
    Spinner locationSpinner;
    Button saveBtn;

    String type;
    String age;
    String size;
    String gender;
    String location;
    String text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_post, container, false);

        String postId = PostFragmentArgs.fromBundle(getArguments()).getPostId();

        //TODO:
        petTextTv = view.findViewById(R.id.edit_post_text);
        petImage = view.findViewById(R.id.edit_post_img);

        // Type
        typeSpinner = view.findViewById(R.id.edit_post_type_spinner);
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
        sizeSpinner = view.findViewById(R.id.edit_post_type_spinner);
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
        genderSpinner = view.findViewById(R.id.edit_post_gender_spinner);
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
        locationSpinner = view.findViewById(R.id.edit_post_location_spinner);
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

        // Age
        ageEt = view.findViewById(R.id.edit_post_age_et);
        age = ageEt.getText().toString();

        // create post btn
        saveBtn = view.findViewById(R.id.edit_post_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pet text
                petTextTv = view.findViewById(R.id.edit_post_text);
                text = petTextTv.getText().toString();

                // Age
                ageEt = view.findViewById(R.id.edit_post_age_et);
                age = ageEt.getText().toString();

                //TODO: Validate user input
                savePost();
            }
        });

        Model.instance.getPostById(postId, new Model.GetPostById() {
            @Override
            public void onComplete(Post post) {
                updatedPost = post;
                if (post.getText()!= null) {
                    petTextTv.setText(post.getText());
                }
                if (post.getAge() != null) {
                    ageEt.setText(post.getAge());
                }
                if (post.getImage() != null){
                    Picasso.get().load(post.getImage()).into(petImage);
                }
                if (post.getGender() != null) {
                    int genderSpinnerPosition = adapterGender.getPosition(gender);
                    genderSpinner.setSelection(genderSpinnerPosition);
                }
            }
        });

        return view;
    }

    private void savePost(){

        Model.instance.savePost(updatedPost, () -> {
            //TODO:navigate up
            NavHostFragment.findNavController(this).navigate(EditPostFragmentDirections.actionNavEditPostToNavProfile());
        });
    }
}