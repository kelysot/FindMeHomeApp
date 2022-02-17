package com.example.findmehomeapp.ui.Post;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.Post;
import com.example.findmehomeapp.Model.User;
import com.example.findmehomeapp.R;
import com.example.findmehomeapp.ui.TimeAgo;
import com.squareup.picasso.Picasso;

public class PostFragment extends Fragment {

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
    ImageView likeImg;
    TextView likesNumberTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        String postId = PostFragmentArgs.fromBundle(getArguments()).getPostId();
        Log.d("TAG33", "fb returned " + postId);

        Model.instance.getPostById(postId, new Model.GetPostById() {
            @Override
            public void onComplete(Post post) {

                String timeAgo = TimeAgo.getTimeAgo(post.getUpdateDate());

                postTimeTv.setText(timeAgo);
                if(!post.getText().equals("")){
                    petTextTv.setText(post.getText());
                }
                else {
                    petTextTv.setVisibility(View.GONE);
                }
                if (post.getAge() != null) {
                    petAge.setText("Age: " + post.getAge());
                    petAge.setVisibility(View.VISIBLE);
                }
                if(post.getGender() != null){
                    petGender.setText("Gender: "+post.getGender());
                    petGender.setVisibility(View.VISIBLE);
                }
                if (post.getLocation() != null) {
                    petLocation.setText("Location: " + post.getLocation());
                    petLocation.setVisibility(View.VISIBLE);
                }
                if (post.getSize() != null) {
                    petSize.setText("Size: " + post.getSize());
                    petSize.setVisibility(View.VISIBLE);
                }
                if (post.getType() != null) {
                    petType.setText("Type: " + post.getType());
                    petType.setVisibility(View.VISIBLE);
                }
                if (post.getImage() != null){
                    Picasso.get().load(post.getImage()).into(petImage);
                }
                Model.instance.getUserById(post.getUserId(), new Model.GetUserById() {
                    @Override
                    public void onComplete(User user) {
                        Log.d("TAG111", "user Id:" + user.getId() );
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

                if(!Model.instance.getConnectedUserId().equals(post.getUserId())){
                    editBtn.setVisibility(View.GONE);
                }
            }
        });

        usernameTv = view.findViewById(R.id.post_username_tv);
        //TODO: age gender location size type
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
        ownerPhoneNum  = view.findViewById(R.id.post_owner_phone);

        editBtn.setOnClickListener((v)->{
            Log.d("TAG33", "fb returned " + postId);
            Navigation.findNavController(v).navigate(PostFragmentDirections.actionNavPostToNavEditPost(postId));
        });
        return view;
    }
}