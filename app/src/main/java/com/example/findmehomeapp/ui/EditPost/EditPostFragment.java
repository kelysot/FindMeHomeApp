package com.example.findmehomeapp.ui.EditPost;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.findmehomeapp.ui.EditProfile.EditProfileFragment;
import com.example.findmehomeapp.ui.Post.PostFragmentArgs;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
    int flag = 0;
    int flagPic = 0;

    int requestCode;
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
        progressBar.getIndeterminateDrawable().setColorFilter(rgb(191, 215, 255), PorterDuff.Mode.MULTIPLY);

        // create post btn
        saveBtn = view.findViewById(R.id.edit_post_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pet text
                viewModel.setText(petTextTv.getText().toString());

                // Age
                viewModel.setAge(ageEt.getText().toString());

                savePost();
            }
        });

        viewModel.GetPostById(postId, post -> {
            viewModel.setData(post);
            if (post.getText() != null) {
                petTextTv.setText(post.getText());
            }
            if (post.getAge() != null) {
                ageEt.setText(post.getAge());
            }
            if (post.getImage() != null) {
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

    private void savePost() {
        progressBar.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(false);
        typeSpinner.setEnabled(false);
        sizeSpinner.setEnabled(false);
        locationSpinner.setEnabled(false);
        genderSpinner.setEnabled(false);
        editImage.setEnabled(false);
        petTextTv.setEnabled(false);
        ageEt.setEnabled(false);

        String age = ageEt.getText().toString();

        if (age.isEmpty()) {
            ageEt.setError("Please enter your pet age.");
            progressBar.setVisibility(View.GONE);
            saveBtn.setEnabled(true);
            typeSpinner.setEnabled(true);
            sizeSpinner.setEnabled(true);
            locationSpinner.setEnabled(true);
            genderSpinner.setEnabled(true);
            editImage.setEnabled(true);
            petTextTv.setEnabled(true);
            ageEt.setEnabled(true);

        } else if (age.length() > 9 || age.length() < 5) {
            ageEt.setError("Please enter your pet age in this form: 2 months or 3 years.");
            progressBar.setVisibility(View.GONE);
            saveBtn.setEnabled(true);
            typeSpinner.setEnabled(true);
            sizeSpinner.setEnabled(true);
            locationSpinner.setEnabled(true);
            genderSpinner.setEnabled(true);
            editImage.setEnabled(true);
            petTextTv.setEnabled(true);
            ageEt.setEnabled(true);
        } else {
            viewModel.setType(type);
            viewModel.setGender(gender);
            viewModel.setLocation(location);
            viewModel.setSize(size);
            String postId = viewModel.data.getValue().getId();

            if (imageBitmap != null) {
                Model.instance.savePostImage(imageBitmap, postId + ".jpg", url -> {
                    if (requestCode == REQUEST_CAMERA) {
                        saveImageToFile(imageBitmap, getLocalImageFileName(url));
                    }
                    viewModel.setImage(url);
                    viewModel.EditPost(new Model.UpdatePostListener() {
                        @Override
                        public void onComplete() {
                            NavHostFragment.findNavController(EditPostFragment.this).navigateUp();
                        }

                        @Override
                        public void onFailure() {
                            progressBar.setVisibility(View.GONE);
                            saveBtn.setEnabled(true);
                            typeSpinner.setEnabled(true);
                            sizeSpinner.setEnabled(true);
                            locationSpinner.setEnabled(true);
                            genderSpinner.setEnabled(true);
                            editImage.setEnabled(true);
                        }
                    });
                });
            } else {
                if (flag == 1 && flagPic == 0) {
                    viewModel.data.getValue().setImage(null);
                    Model.instance.deleteImagePost(viewModel.data.getValue().getId() + ".jpg", () -> {
                        viewModel.EditPost(new Model.UpdatePostListener() {
                            @Override
                            public void onComplete() {
                                NavHostFragment.findNavController(EditPostFragment.this).navigateUp();
                            }

                            @Override
                            public void onFailure() {
                                progressBar.setVisibility(View.GONE);
                                saveBtn.setEnabled(true);
                                typeSpinner.setEnabled(true);
                                sizeSpinner.setEnabled(true);
                                locationSpinner.setEnabled(true);
                                genderSpinner.setEnabled(true);
                                editImage.setEnabled(true);
                            }
                        });
                    });
                }else {
                    viewModel.EditPost(new Model.UpdatePostListener() {
                        @Override
                        public void onComplete() {
                            NavHostFragment.findNavController(EditPostFragment.this).navigateUp();
                        }

                        @Override
                        public void onFailure() {
                            progressBar.setVisibility(View.GONE);
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

        }
    }

    public void deletePost() {
        progressBar.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(false);
        typeSpinner.setEnabled(false);
        sizeSpinner.setEnabled(false);
        locationSpinner.setEnabled(false);
        genderSpinner.setEnabled(false);
        editImage.setEnabled(false);
        petTextTv.setEnabled(false);
        ageEt.setEnabled(false);
        if(viewModel.data.getValue().getImage() != null){
            Model.instance.deleteImagePost(viewModel.data.getValue().getId() + ".jpg", new Model.DeleteImagePostListener() {
                @Override
                public void onComplete() {
                    viewModel.DeletePost(() -> {
                        NavHostFragment.findNavController(EditPostFragment.this).navigate(R.id.action_global_nav_profile);
                    });
                }
            });
        }
        else{
            viewModel.DeletePost(() -> {
                NavHostFragment.findNavController(EditPostFragment.this).navigate(R.id.action_global_nav_profile);
            });
        }

    }

    private void showImagePickDialog() {

        viewModel.GetPostById(viewModel.data.getValue().getId(), post -> {
            if(post.getImage() != null || flagPic == 1) {
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
                        if (i == 2) {
                            deleteImage();
                        }
                    }
                });

                builder.create().show();
            } else {
                flagPic = 1;
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
        });
    }

    private void deleteImage() {
        imageBitmap = null;
        petImage.setImageBitmap(null);
        petImage.setBackgroundResource(R.drawable.cat_dog_rabbit);
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
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}