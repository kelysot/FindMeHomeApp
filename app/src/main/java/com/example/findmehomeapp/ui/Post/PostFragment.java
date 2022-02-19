package com.example.findmehomeapp.ui.Post;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.User;
import com.example.findmehomeapp.R;
import com.example.findmehomeapp.ui.TimeAgo;
import com.squareup.picasso.Picasso;

public class PostFragment extends Fragment {

    PostViewModel viewModel;

    ImageView profileImage;
    ImageButton editBtn;
    TextView usernameTv;
    TextView postTimeTv;
    TextView petTextTv;
    TextView petAge;
    TextView petLocation;
    TextView petSize;
    TextView petType;
    TextView petGender;
    TextView ownerPhone;
    TextView ownerPhoneNum;
    ImageView petImage;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(PostViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        String postId = PostFragmentArgs.fromBundle(getArguments()).getPostId();

        viewModel.GetPostById(postId, post -> {
            viewModel.setData(post);

            String timeAgo = TimeAgo.getTimeAgo(viewModel.getData().getUpdateDate());

            postTimeTv.setText(timeAgo);
            if (!viewModel.getData().getText().equals("")) {
                petTextTv.setText(viewModel.getData().getText());
            } else {
                petTextTv.setVisibility(View.GONE);
            }
            if (viewModel.getData().getAge() != null) {
                petAge.setText("Age: " + viewModel.getData().getAge());
                petAge.setVisibility(View.VISIBLE);
            }
            if (viewModel.getData().getGender() != null) {
                petGender.setText("Gender: " + viewModel.getData().getGender());
                petGender.setVisibility(View.VISIBLE);
            }
            if (viewModel.getData().getLocation() != null) {
                petLocation.setText("Location: " + viewModel.getData().getLocation());
                petLocation.setVisibility(View.VISIBLE);
            }
            if (viewModel.getData().getSize() != null) {
                petSize.setText("Size: " + viewModel.getData().getSize());
                petSize.setVisibility(View.VISIBLE);
            }
            if (viewModel.getData().getType() != null) {
                petType.setText("Type: " + viewModel.getData().getType());
                petType.setVisibility(View.VISIBLE);
            }
            if (viewModel.getData().getImage() != null) {
                Picasso.get().load(viewModel.getData().getImage()).into(petImage);
            }

            Model.instance.getUserById(viewModel.getData().getUserId(), new Model.GetUserById() {
                @Override
                public void onComplete(User user) {
                    if (user.getAvatarUrl() != null) {
                        Picasso.get().load(user.getAvatarUrl()).into(profileImage);
                    }
                    if (user.getName() != null) {
                        usernameTv.setText(user.getName());
                        ownerPhone.setText(user.getName() + "'s phone:");
                    }
                    ownerPhoneNum.setText(user.getPhone());
                }
            });

            if (!Model.instance.getConnectedUserId().equals(viewModel.getData().getUserId())) {
                editBtn.setVisibility(View.GONE);
            }
        });

        usernameTv = view.findViewById(R.id.post_username_tv);
        postTimeTv = view.findViewById(R.id.post_post_time_tv);
        petTextTv = view.findViewById(R.id.post_pet_text_tv);
        profileImage = view.findViewById(R.id.post_user_img);
        petImage = view.findViewById(R.id.post_pet_img);
        editBtn = view.findViewById(R.id.post_edit_btn);
        petAge = view.findViewById(R.id.post_age_tv);
        petGender = view.findViewById(R.id.post_gender_tv);
        petLocation = view.findViewById(R.id.post_location_tv);
        petSize = view.findViewById(R.id.post_size_tv);
        petType = view.findViewById(R.id.post_type_tv);
        ownerPhone = view.findViewById(R.id.post_owner_details_phone);
        ownerPhoneNum = view.findViewById(R.id.post_owner_phone);

        editBtn.setOnClickListener((v) -> {
            Navigation.findNavController(v).navigate(PostFragmentDirections.actionNavPostToNavEditPost(postId));
        });
        return view;
    }
}