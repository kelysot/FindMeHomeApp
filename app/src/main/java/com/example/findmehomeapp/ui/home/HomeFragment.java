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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.example.findmehomeapp.databinding.FragmentHomeBinding;
import com.example.findmehomeapp.ui.Profile.ProfileFragmentArgs;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    MyAdapter adapter;
    TextView nameTv;
    ImageView avatarImv;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    SwipeRefreshLayout swipeRefresh;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        //String userId = HomeFragmentArgs.fromBundle(getArguments()).getUserId();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Model.instance.getUserById(userId, new Model.GetUserById() {
            @Override
            public void onComplete(User user) {
                nameTv.setText("Hey, " + user.getName() + " \uD83D\uDC4B");
                if (user.getAvatarUrl() != null) {
                    Picasso.get().load(user.getAvatarUrl()).into(avatarImv);
                }
            }
        });

        nameTv = view.findViewById(R.id.home_username_tv);
        avatarImv = view.findViewById(R.id.home_user_img);

        swipeRefresh = view.findViewById(R.id.postslist_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> Model.instance.refreshPostsList(userId));

        RecyclerView list = view.findViewById(R.id.home_post_rv) ;
        list.setHasFixedSize(true);

        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String postId = homeViewModel.getData().getValue().get(position).getId();
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
        TextView petTextTv;
//        TextView idTv;
//        CheckBox cb;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            petTextTv = itemView.findViewById(R.id.post_pet_text_tv);
//            idTv = itemView.findViewById(R.id.listrow_id_tv);
//            cb = itemView.findViewById(R.id.listrow_cb);
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
            holder.petTextTv.setText(post.getText());
//            holder.idTv.setText(student.getId());
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
            firebaseAuth.signOut();
            NavHostFragment.findNavController(this).navigate(HomeFragmentDirections.actionNavHomeToNavLogin());
            return true;
        } else {
            return super.onOptionsItemSelected(item);

        }
    }
}