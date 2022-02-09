package com.example.findmehomeapp.ui.EditProfile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.User;
import com.example.findmehomeapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.IOException;


public class EditProfileFragment extends Fragment {

    EditText nameEt;
    EditText phoneEt;
    Spinner locationSpinner;
    Button saveBtn;
    NavController navController;
    String userid;
    String locationS;
    ImageView picture;
    ImageView addPicture;
    Bitmap imageBitmap;
    FirebaseAuth firebaseAuth;
    String userId;
    String myLocation = "";
    String img = null;
    int locationPos;

    String userEmail;
    String userPass;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

//        String stId = ProfileFragmentArgs.fromBundle(getArguments()).getUserId();
        userId = firebaseAuth.getCurrentUser().getUid();

        Model.instance.getUserById(userId, new Model.GetUserById() {
            @Override
            public void onComplete(User user) {
                nameEt.setText(user.getName());
                phoneEt.setText(user.getPhone());
                userEmail = user.getEmail();
                userPass = user.getPassword();
                if (user.getAvatarUrl() != null) {
                    img = user.getAvatarUrl();
                    Picasso.get().load(user.getAvatarUrl()).into(picture);
                }
            }
        });


        picture = view.findViewById(R.id.edit_profile_user_imageView);
        addPicture = view.findViewById(R.id.edit_profile_add_pic_imv);
        nameEt = view.findViewById(R.id.edit_profile_et_name);
        phoneEt = view.findViewById(R.id.edit_profile_phone_number);

        locationSpinner = view.findViewById(R.id.edit_profile_location_spinner);
        ArrayAdapter<CharSequence> adapterLocation = ArrayAdapter.createFromResource(this.getContext(),
                R.array.location, android.R.layout.simple_spinner_item);
        adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapterLocation);
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locationS = parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), gender, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        saveBtn = view.findViewById(R.id.edit_profile_btn_register);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        return view;
    }

    private void showImagePickDialog() {

        String[] items = {"Camera", "Gallery", "Delete Photo"};
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
                //TODO:Delete photo.
                if (i == 2){
                    deleteImage();
                }
            }
        });

        builder.create().show();
    }

    private void deleteImage() {
        imageBitmap = null;
        //  if()
        picture.setImageBitmap(null);
        picture.setBackgroundResource(R.drawable.user);
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
                picture.setImageBitmap(imageBitmap);
                Log.d("TAG33", "imageBitmap name:" + imageBitmap);

            }
        }
        if (requestCode == REQUEST_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        picture.setImageBitmap(imageBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();

            }
        }
    }


    //To show user's location information from the database.
    @Override
    public void onResume() {
        super.onResume();
        Model.instance.getUserById(userId, new Model.GetUserById() {
            @Override
            public void onComplete(User user) {
                myLocation = user.getLocation();
                switch (myLocation) {
                    case "Center":
                        locationPos = 0;
                        break;
                    case "North":
                        locationPos = 1;
                        break;

                    case "South":
                        locationPos = 2;
                        break;
                }
                locationSpinner.setSelection(locationPos);
            }
        });
    }

    public void save(){
        saveBtn.setEnabled(false);

        String fullName = nameEt.getText().toString();
        String phone = phoneEt.getText().toString();

        //TODO:tell the user if his email already exist he can't edit his email
        if (fullName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getContext(), "Fields are required", Toast.LENGTH_SHORT).show();
        }

        if (fullName.isEmpty()) {
            nameEt.setError("Enter a name");
        }

        if (phone.isEmpty()) {
            phoneEt.setError("Enter a phone");
        }

        User user = new User(userId, fullName, phone, userEmail, userPass, locationS, "true");
        user.setAvatarUrl(img);

        if (imageBitmap == null) {
            if(img != null){
                user.setAvatarUrl(null);
                Model.instance.deleteImage(userEmail + ".jpg", ()->{
                    Model.instance.editUser(user, () -> {
                        NavHostFragment.findNavController(this).navigate(R.id.action_global_nav_profile);
                    });
                });
            }
            else{
                Model.instance.editUser(user, () -> {
                    NavHostFragment.findNavController(this).navigate(R.id.action_global_nav_profile);
                });
            }
        } else {
            Model.instance.saveImage(imageBitmap, userEmail + ".jpg", url -> {
                user.setAvatarUrl(url);
                Model.instance.editUser(user, () -> {
                    NavHostFragment.findNavController(this).navigate(R.id.action_global_nav_profile);
                });
            });
        }


    }


}