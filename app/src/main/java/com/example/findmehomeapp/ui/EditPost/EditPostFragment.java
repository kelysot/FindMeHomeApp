package com.example.findmehomeapp.ui.EditPost;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.Post;
import com.example.findmehomeapp.Model.User;
import com.example.findmehomeapp.R;
import com.example.findmehomeapp.ui.Post.PostFragmentArgs;
import com.example.findmehomeapp.ui.Post.PostFragmentDirections;
import com.squareup.picasso.Picasso;

public class EditPostFragment extends Fragment {
    TextView petTextTv;
    ImageView petImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_post, container, false);

        String postId = PostFragmentArgs.fromBundle(getArguments()).getPostId();

        Model.instance.getPostById(postId, new Model.GetPostById() {
            @Override
            public void onComplete(Post post) {
                petTextTv.setText(post.getText());
                if (post.getImage() != null){
                    Picasso.get().load(post.getImage()).into(petImage);
                }
            }
        });

        //TODO:
        petTextTv = view.findViewById(R.id.edit_post_text);
        petImage = view.findViewById(R.id.edit_post_img);

        return view;
    }
}