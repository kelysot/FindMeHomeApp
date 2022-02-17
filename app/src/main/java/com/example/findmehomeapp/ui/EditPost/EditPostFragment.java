package com.example.findmehomeapp.ui.EditPost;

import static android.graphics.Color.alpha;
import static android.graphics.Color.rgb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.Post;
import com.example.findmehomeapp.Model.User;
import com.example.findmehomeapp.R;
import com.example.findmehomeapp.ui.CreatePost.CreatePostFragmentDirections;
import com.example.findmehomeapp.ui.EditProfile.EditProfileFragment;
import com.example.findmehomeapp.ui.EditProfile.EditProfileViewModel;
import com.example.findmehomeapp.ui.Post.PostFragmentArgs;
import com.example.findmehomeapp.ui.Post.PostFragmentDirections;
import com.example.findmehomeapp.ui.home.HomeFragment;
import com.example.findmehomeapp.ui.home.HomeFragmentDirections;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class EditPostFragment extends Fragment {
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    
    FirebaseAuth firebaseAuth;

    EditPostViewModel viewModel;

    EditText petTextTv;
    ImageView petImage;
    Spinner typeSpinner;
    EditText ageEt;
    Spinner sizeSpinner;
    Spinner genderSpinner;
    Spinner locationSpinner;
    Button saveBtn;
    ImageView editImage;
    ProgressBar progressBar;
    String type;
    String size;
    String gender;
    String location;


    Bitmap imageBitmap;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(EditPostViewModel.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_post, container, false);

        String postId = PostFragmentArgs.fromBundle(getArguments()).getPostId();

        ageEt = view.findViewById(R.id.edit_post_age_et);
        petTextTv = view.findViewById(R.id.edit_post_text);
        petImage = view.findViewById(R.id.edit_post_img);
        editImage = view.findViewById(R.id.edit_post_change_image);

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

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
        sizeSpinner = view.findViewById(R.id.edit_post_size_spinner);
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

        progressBar = view.findViewById(R.id.edit_post_progressBar);
        progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(rgb(191,215,255), PorterDuff.Mode.MULTIPLY);

        // create post btn
        saveBtn = view.findViewById(R.id.edit_post_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pet text
                viewModel.setText(petTextTv.getText().toString());

                // Age
                viewModel.setAge(ageEt.getText().toString());

                if(imageBitmap != null){
                    String userId = firebaseAuth.getCurrentUser().getUid();
                    Model.instance.saveImage(imageBitmap, userId + ".jpg", url -> {
                        viewModel.setImage(url);
                        savePost();
                    });
                } else {
                    savePost();
                }
            }
        });

        viewModel.GetPostById(postId, post -> {
            viewModel.setData(post);
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
                int SpinnerPosition = adapterGender.getPosition(post.getGender());
                genderSpinner.setSelection(SpinnerPosition);
            }
            if (post.getType() != null) {
                int SpinnerPosition = adapterType.getPosition(post.getType());
                typeSpinner.setSelection(SpinnerPosition);
            }
            if (post.getLocation() != null) {
                int SpinnerPosition = adapterLocation.getPosition(post.getLocation());
                locationSpinner.setSelection(SpinnerPosition);
            }
            if (post.getSize() != null) {
                int SpinnerPosition = adapterSize.getPosition(post.getSize());
                sizeSpinner.setSelection(SpinnerPosition);
            }
        });
        setHasOptionsMenu(true);

        return view;
    }

    private void savePost(){
        progressBar.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(false);
        typeSpinner.setEnabled(false);
        sizeSpinner.setEnabled(false);
        locationSpinner.setEnabled(false);
        genderSpinner.setEnabled(false);
        editImage.setEnabled(false);
        setHasOptionsMenu(true);

        String age = ageEt.getText().toString();

        if (age.isEmpty()) {
            ageEt.setError("Please enter your pet age.");
            progressBar.setVisibility(View.GONE);
            setHasOptionsMenu(false);
            saveBtn.setEnabled(true);
            typeSpinner.setEnabled(true);
            sizeSpinner.setEnabled(true);
            locationSpinner.setEnabled(true);
            genderSpinner.setEnabled(true);
            editImage.setEnabled(true);
        }
        else {
            viewModel.setType(type);
            viewModel.setGender(gender);
            viewModel.setLocation(location);
            viewModel.setSize(size);

            viewModel.EditPost(new Model.UpdatePostListener() {
                @Override
                public void onComplete() {
                    NavHostFragment.findNavController(EditPostFragment.this).navigateUp();
                }
                @Override
                public void onFailure() {
                    progressBar.setVisibility(View.GONE);
                    setHasOptionsMenu(false);
                    saveBtn.setEnabled(true);
                    typeSpinner.setEnabled(true);
                    sizeSpinner.setEnabled(true);
                    locationSpinner.setEnabled(true);
                    genderSpinner.setEnabled(true);
                    editImage.setEnabled(true);
                }
            });
        }
    }

    public void deletePost(){
        progressBar.setVisibility(View.VISIBLE);
        setHasOptionsMenu(false);
        saveBtn.setEnabled(false);
        typeSpinner.setEnabled(false);
        sizeSpinner.setEnabled(false);
        locationSpinner.setEnabled(false);
        genderSpinner.setEnabled(false);
        editImage.setEnabled(false);

        viewModel.DeletePost(() -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_global_nav_profile);
        });
    }

    private void showImagePickDialog() {

        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Choose an Option");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                if (i == 0) {
                    openCam();
                }

                if (i == 1) {
                    openGallery();
                }
            }
        });

        builder.create().show();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);
    }

    private void openCam() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                petImage.setImageBitmap(imageBitmap);
                Log.d("TAG33", "imageBitmap name:" + imageBitmap);

            }
        }
        if (requestCode == REQUEST_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        petImage.setImageBitmap(imageBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_post_delete_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_post) {
            deletePost();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }
}