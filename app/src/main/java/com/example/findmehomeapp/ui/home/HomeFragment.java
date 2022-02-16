package com.example.findmehomeapp.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.ModelFirebase;
import com.example.findmehomeapp.Model.Post;
import com.example.findmehomeapp.Model.User;
import com.example.findmehomeapp.R;
//import com.example.findmehomeapp.databinding.FragmentHomeBinding;
import com.example.findmehomeapp.ui.TimeAgo;
import com.example.findmehomeapp.ui.Post.PostFragmentDirections;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    MyAdapter adapter;
    TextView nameTv;
    CircleImageView avatarImv;
//    Button addPostBtn;

    SwipeRefreshLayout swipeRefresh;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                    this.setEnabled(false);
                    requireActivity().finish();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        String userId = Model.instance.getConnectedUserId();
        Log.d("TAG13", "login:" + userId);

        homeViewModel.GetUserById(Model.instance.getConnectedUserId(), user -> {
            homeViewModel.setUserData(user);
            nameTv.setText("Hey, " + user.getName() + " \uD83D\uDC4B");
            if (user.getAvatarUrl() != null) {
                Picasso.get().load(user.getAvatarUrl()).into(avatarImv);
            }
        });

        nameTv = view.findViewById(R.id.home_username_tv);
        avatarImv = view.findViewById(R.id.home_user_img);
//        addPostBtn = view.findViewById(R.id.home_btn_add_post);
//
//        addPostBtn.setOnClickListener((v)->{
//            Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_create_post);
//        });

        swipeRefresh = view.findViewById(R.id.postslist_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> Model.instance.refreshPostsList());
        Model.instance.refreshPostsList();

        RecyclerView list = view.findViewById(R.id.home_post_rv) ;
        list.setHasFixedSize(true);

        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String postId = homeViewModel.getData().getValue().get(position).getId().toString();
                Navigation.findNavController(v).navigate(HomeFragmentDirections.actionNavHomeToNavPost(postId));
            }
        });

        setHasOptionsMenu(true);
        homeViewModel.getData().observe(getViewLifecycleOwner(), list1 -> refresh());

        swipeRefresh.setRefreshing(Model.instance.getPostListLoadingState().getValue() == Model.PostListLoadingState.loading);

        Model.instance.getPostListLoadingState().observe(getViewLifecycleOwner(), postListLoadingState -> {
            if(postListLoadingState == Model.PostListLoadingState.loading) {
                swipeRefresh.setRefreshing(true);
            } else {
                swipeRefresh.setRefreshing(false);
            }
        });

        return view;
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        //TODO: add heart
        TextView petTextTv;
        ImageView petImage;
        CircleImageView userImage;
        TextView userName;
        TextView postTime;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            petTextTv = itemView.findViewById(R.id.post_pet_text_tv);
            petImage = itemView.findViewById(R.id.post_pet_img);
            userImage = itemView.findViewById(R.id.post_user_img);
            userName = itemView.findViewById(R.id.post_user_name);
            postTime = itemView.findViewById(R.id.post_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    listener.onItemClick(v, pos);
                }
            });
        }
    }

    interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
    class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        OnItemClickListener listener;
        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.post_list_row,parent,false);
            MyViewHolder holder = new MyViewHolder(view,listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Post post = homeViewModel.getData().getValue().get(position);
            //TODO: set relevants from holder
            if(post.getText().equals("")){
                holder.petTextTv.setVisibility(View.GONE);
            }
            else {
                holder.petTextTv.setText(post.getText());
            }

            String timeAgo = TimeAgo.getTimeAgo(post.getUpdateDate());
            holder.postTime.setText(timeAgo);

            if (post.getImage() != null) {
                Picasso.get().load(post.getImage()).into(holder.petImage);
            }

            homeViewModel.GetUserById(post.getUserId(), user -> {
                holder.userName.setText(user.getName());
                if (user.getAvatarUrl() != null) {
                    Picasso.get().load(user.getAvatarUrl()).into(holder.userImage);
                }
            });

        }

        @Override
        public int getItemCount() {
            if(homeViewModel.getData().getValue() == null) {
                return 0;
            }
            return homeViewModel.getData().getValue().size();
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_logout_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            Log.d("TAG55", "logout...");
            Model.instance.getUserById(Model.instance.getConnectedUserId(), new Model.GetUserById() {
                @Override
                public void onComplete(User user) {
                    if (user.getConnected().equals("true")) {
                        user.setConnected("false");
                        Model.instance.editUser(user, new Model.EditUserListener() {
                            @Override
                            public void onComplete() {
                                Model.instance.logout(()->{
                                    NavHostFragment.findNavController(HomeFragment.this).navigate(HomeFragmentDirections.actionNavHomeToNavLogin());
                                });
                            }

                            @Override
                            public void onFailure() {

                            }
                        });
                    }
                }
            });


            // firebaseAuth.signOut();
            return true;
        }
        else if (item.getItemId() == R.id.add_post) {
            Log.d("TAG55", "logout...");
            NavHostFragment.findNavController(HomeFragment.this).navigate(HomeFragmentDirections.actionNavHomeToNavCreatePost());
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);

        }
    }

}