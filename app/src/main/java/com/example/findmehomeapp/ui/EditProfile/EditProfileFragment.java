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
    Spinner genderSpinner;
    Button saveBtn;
    NavController navController;
    String userid;
    String genderS;
    ImageView picture;
    ImageView addPicture;
    Bitmap imageBitmap;
    String userId;
    String myGender = "";
    String img = null;
    int genderPos;
    int flag = 0;

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

//        String stId = ProfileFragmentArgs.fromBundle(getArguments()).getUserId();
        //userId = Model.instance.getConnectedUserId();

        Model.instance.getUserById(Model.instance.getConnectedUserId(), new Model.GetUserById() {
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

        genderSpinner = view.findViewById(R.id.edit_profile_gender_spinner);
        ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(this.getContext(),
                R.array.gender, android.R.layout.simple_spinner_item);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapterGender);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderS = parent.getItemAtPosition(position).toString();
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
        flag = 1;
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


    //To show user's gender information from the database.
    @Override
    public void onResume() {
        super.onResume();
        Model.instance.getUserById(Model.instance.getConnectedUserId(), new Model.GetUserById() {
            @Override
            public void onComplete(User user) {
                myGender = user.getGender();
                switch (myGender) {
                    case "Woman":
                        genderPos = 0;
                        break;
                    case "Man":
                        genderPos = 1;
                        break;
                }
                genderSpinner.setSelection(genderPos);
            }
        });
    }

    public void save(){

        String fullName = nameEt.getText().toString();
        String phone = phoneEt.getText().toString();

        if (fullName.isEmpty()) {
            nameEt.setError("Please enter your full name.");
        }
        else if (phone.isEmpty()) {
            phoneEt.setError("Please enter your phone number.");
        }
        else{
           // saveBtn.setEnabled(true);
            User user = new User(Model.instance.getConnectedUserId(), fullName, phone, userEmail, userPass, genderS, "true");
            user.setAvatarUrl(img);

            if (imageBitmap == null) {
                if(flag == 1){
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


}