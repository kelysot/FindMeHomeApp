package com.example.findmehomeapp.ui.Register;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
import android.widget.Spinner;
import android.widget.Toast;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;

public class RegisterFragment extends Fragment {
    // TODO: add image

    EditText nameEt;
    EditText phoneEt;
    EditText emailEt;
    EditText passwordEt;
    EditText repasswordEt;
    Spinner locationSpinner;
    Button registerBtn;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    NavController navController;
    String userid;
    String locationS;
    ImageView picture;
    ImageView addPicture;
    Bitmap imageBitmap;


    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;


    public RegisterFragment(){}

    //Executor executor = Executors.newFixedThreadPool(1);

    //ModelFirebase modelFirebase = new ModelFirebase();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        picture = view.findViewById(R.id.register_user_imageView);
        addPicture = view.findViewById(R.id.register_add_pic_imv);
        nameEt = view.findViewById(R.id.register_et_name);
        phoneEt = view.findViewById(R.id.register_phone_number);
        emailEt = view.findViewById(R.id.register_et_email);
        passwordEt = view.findViewById(R.id.register_et_password);
        repasswordEt = view.findViewById(R.id.register_et_repassword);

        locationSpinner = view.findViewById(R.id.register_location_spinner);
        ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(this.getContext(),
                R.array.location, android.R.layout.simple_spinner_item);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapterGender);
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

        registerBtn = view.findViewById(R.id.register_btn_register);
        navController = Navigation.findNavController(view);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validateAndSave();

                String email = emailEt.getText().toString().trim();
                String password = passwordEt.getText().toString().trim();
                String fullName = nameEt.getText().toString();
                String phone = phoneEt.getText().toString();
                String repassword = repasswordEt.getText().toString();


                //TODO:tell the user if his email already exist he can't register
                if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {

                    Toast.makeText(getContext(), "Fields are required", Toast.LENGTH_SHORT).show();
                }

                if (fullName.isEmpty()) {

                    nameEt.setError("Enter a name");
                }

                if (password.length() < 6) {

                    passwordEt.setError("Password Length Must Be 6 or more Chars");
                }

//                if(!password.equals(repassword)){
//                    passwordEt.setError("Password and Re-password need to be the same");
//
//                }

                if (email.isEmpty()) {

                    emailEt.setError("Enter email Bitch");

                }

                if(phone.isEmpty()){
                    phoneEt.setError("Enter a phone");

                }

                signUptheUser(fullName, email, password, phone, locationS);

            }
        });




//        gotoSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                navController.navigate(R.id.action_registerFragment_to_loginFragment);
//            }
//        });


    }

    private void showImagePickDialog() {

        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Choose an Option");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                if (i==0) {

                    openCam();
                }

                if (i==1) {

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
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_GALLERY);
    }

    private void openCam() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA){
            if (resultCode == Activity.RESULT_OK){
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                picture.setImageBitmap(imageBitmap);
                Log.d("TAG33","imageBitmap name:" + imageBitmap );

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

    private void signUptheUser(String name, String email, String password, String phone, String location) {

        Model.instance.saveImage(imageBitmap, email + ".jpg", url -> {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        userid = firebaseAuth.getCurrentUser().getUid();

                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("userid", userid);
                        //   hashMap.put("imageUrl", imageUri.toString());
                        hashMap.put("username", name);
                        hashMap.put("phone", phone);
                        hashMap.put("location", location);
                        hashMap.put("imageUri", url);

                        firestore.collection("Users").document(userid).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });


                        Log.d("TAG", "saved name:" + name + "user Id:" + userid);
                        //navController.navigate(RegisterFragmentDirections.actionGlobalNavProfile(userid));
                        navController.navigate(R.id.action_global_nav_profile);


                    }

                }
            });
        });
    }


//    private void validateAndSave(){
//        registerBtn.setEnabled(false);
//        String name = nameEt.getText().toString();
//        String phone = phoneEt.getText().toString();
//        String email = emailEt.getText().toString();
//        String password = "";//passwordEt.getText().toString();
//        String repassword = "";//repasswordEt.getText().toString();
//        String gender = "";//genderSpinner.getSelectedItem().toString();
//        String age = "";//ageSpinner.getSelectedItem().toString();
//
//        User user = new User(name,phone,email,password,gender,age);
//
//        modelFirebase.getAllUsers(new ModelFirebase.GetAllUsersListener() {
//            @Override
//            public void onComplete(List<User> list) {
//            }
//        });
//        //TODO: check if user email is in the list of users
////        if (email != "") {
//            // save user in firebase
//            Model.instance.addUser(user,()->{
//                Navigation.findNavController(nameEt).navigate(RegisterFragmentDirections.actionNavRegisterToNavProfile(user.getEmail()));
//            });
//
//            Log.d("TAG", "user has registered");
////        } else {
////            //TODO: alert to user that the email is already in use
////        }
//    }
}