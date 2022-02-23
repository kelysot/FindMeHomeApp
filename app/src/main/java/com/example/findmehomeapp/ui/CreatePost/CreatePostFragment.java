package com.example.findmehomeapp.ui.CreatePost;

import static android.graphics.Color.rgb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.Post;
import com.example.findmehomeapp.MyApplication;
import com.example.findmehomeapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class CreatePostFragment extends Fragment {

    CreatePostViewModel viewModel;

    EditText petTextEt;
    ImageView petImage;
    ImageView addImage;
    int requestCode;
    Bitmap imageBitmap;

    EditText ageEt;
    Spinner typeSpinner;
    Spinner sizeSpinner;
    Spinner genderSpinner;
    Spinner locationSpinner;
    ProgressBar progressBar;

    Button creteBtn;

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(CreatePostViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);

        Post post = new Post(Model.instance.getConnectedUserId(), null, null, null, null, null, null, null, false);
        viewModel.setData(post);

        petImage = view.findViewById(R.id.create_post_photo);
        addImage = view.findViewById(R.id.create_post_add_photo_btn);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        // Type
        typeSpinner = view.findViewById(R.id.create_post_type_spinner);
        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this.getContext(),
                R.array.types, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapterType);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setType(parent.getItemAtPosition(position).toString());
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
                viewModel.setSize(parent.getItemAtPosition(position).toString());
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
                viewModel.setGender(parent.getItemAtPosition(position).toString());
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
                viewModel.setLocation(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        progressBar = view.findViewById(R.id.create_post_progressBar);
        progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(rgb(191, 215, 255), PorterDuff.Mode.MULTIPLY);

        // create post btn
        creteBtn = view.findViewById(R.id.create_post_btn);
        creteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pet text
                petTextEt = view.findViewById(R.id.create_post_text);
                viewModel.setText(petTextEt.getText().toString());

                // Age
                ageEt = view.findViewById(R.id.create_post_age_et);
                viewModel.setAge(ageEt.getText().toString());

                String id = UUID.randomUUID().toString();
                viewModel.data.getValue().setId(id);
                savePost();
            }
        });

        return view;
    }

    private void savePost() {
        progressBar.setVisibility(View.VISIBLE);
        creteBtn.setEnabled(false);
        addImage.setEnabled(false);
        typeSpinner.setEnabled(false);
        sizeSpinner.setEnabled(false);
        locationSpinner.setEnabled(false);
        genderSpinner.setEnabled(false);
        petTextEt.setEnabled(false);
        ageEt.setEnabled(false);

        String age = ageEt.getText().toString();

        if (age.isEmpty()) {
            ageEt.setError("Please enter your pet age.");
            progressBar.setVisibility(View.GONE);
            creteBtn.setEnabled(true);
            typeSpinner.setEnabled(true);
            sizeSpinner.setEnabled(true);
            locationSpinner.setEnabled(true);
            genderSpinner.setEnabled(true);
            addImage.setEnabled(true);
            petTextEt.setEnabled(true);
            ageEt.setEnabled(true);

        } else if (age.length() > 9 || age.length() < 5) {
            ageEt.setError("Please enter your pet age in this form: 2 months or 3 years.");
            progressBar.setVisibility(View.GONE);
            creteBtn.setEnabled(true);
            typeSpinner.setEnabled(true);
            sizeSpinner.setEnabled(true);
            locationSpinner.setEnabled(true);
            genderSpinner.setEnabled(true);
            addImage.setEnabled(true);
            petTextEt.setEnabled(true);
            ageEt.setEnabled(true);
        } else {
            if (imageBitmap != null){
                Model.instance.savePostImage(imageBitmap, viewModel.getPostId() + ".jpg", url -> {
                    if (requestCode == REQUEST_CAMERA) {
                        saveImageToFile(imageBitmap, getLocalImageFileName(url));
                    }
                    viewModel.setImage(url);
                    viewModel.savePost(() -> {
                        NavHostFragment.findNavController(this).navigateUp();
                    });
                });
            }
            else {
                viewModel.savePost(() -> {
                    NavHostFragment.findNavController(this).navigateUp();
                });
            }
        }
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

    private void saveImageToFile(Bitmap imageBitmap, String imageFileName){
        try {
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File imageFile = new File(dir,imageFileName);
            imageFile.createNewFile();
            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            addPicureToGallery(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addPicureToGallery(File imageFile){
        //add the picture to the gallery so we dont need to manage the cache size
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        MyApplication.getContext().sendBroadcast(mediaScanIntent);
    }

    private String getLocalImageFileName(String url) {
        String name = URLUtil.guessFileName(url, null, null);
        return name;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.requestCode = requestCode;
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                petImage.setImageBitmap(imageBitmap);

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
}