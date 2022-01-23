package com.example.findmehomeapp.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.findmehomeapp.MyApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Model {
    public static final Model instance = new Model();

    Executor executor = Executors.newFixedThreadPool(1);
    Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());
    FirebaseAuth firebaseAuth;

    public enum PostListLoadingState{
        loading,
        loaded
    }
    MutableLiveData<PostListLoadingState> postListLoadingState = new MutableLiveData<PostListLoadingState>();
    public LiveData<PostListLoadingState> getPostListLoadingState() {
        return postListLoadingState;
    }

    ModelFirebase modelFirebase = new ModelFirebase();

    private Model(){
        postListLoadingState.setValue(PostListLoadingState.loaded);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    MutableLiveData<List<Post>> postsList = new MutableLiveData<List<Post>>();
    MutableLiveData<List<Post>> userPostsList = new MutableLiveData<List<Post>>();
    MutableLiveData<List<User>> usersList = new MutableLiveData<List<User>>();

    public LiveData<List<Post>> getAllPosts() {
        if (postsList.getValue() == null ) {
            refreshPostsList();
        }
        return postsList;
    }

    public LiveData<List<Post>> getAllUserPosts() {
        if (userPostsList.getValue() == null ) {
            refreshPostsList();
        }
        return userPostsList;
    }

    public void filterUserPostsList() {
        String userId = firebaseAuth.getCurrentUser().getUid();

        List<Post> posts = postsList.getValue();
        List<Post> filteredPosts = new ArrayList<Post>();

        if (posts != null) {
            for (Post post: posts) {
                if (post.getUserId().equals(userId)) {
                    filteredPosts.add(post);
                }
            }
        }

        userPostsList.postValue(filteredPosts);
    }

    public LiveData<List<User>> getAllUsers(){
        if (usersList.getValue() == null) {
            refreshUserList();
        }
        ;
        return usersList;
    }

    public void refreshPostsList() {
        postListLoadingState.setValue(PostListLoadingState.loading);

        // get last local update date
//        Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("PostsLastUpdateDate",0);

        // firebase get all updates since lastLocalUpdateDate
        modelFirebase.getAllPosts(new ModelFirebase.GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> list) {
                // add all records to the local db
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
//                        Long lud = new Long(0);
                        Log.d("TAG","fb returned " + list.size());
                        for (Post post: list) {
                            AppLocalDb.db.postDao().insertAll(post);
//                            if (lud < student.getUpdateDate()){
//                                lud = student.getUpdateDate();
//                            }
                        }
                        // update last local update date
                        MyApplication.getContext()
                                .getSharedPreferences("TAG",Context.MODE_PRIVATE)
                                .edit()
//                                .putLong("PostsLastUpdateDate",lud)
                                .commit();

                        //return all data to caller
                        List<Post> stList = AppLocalDb.db.postDao().getAll();
                        postsList.postValue(stList);

                        filterUserPostsList();

                        postListLoadingState.postValue(PostListLoadingState.loaded);
                    }
                });
            }
        });
    }


    public void refreshUserList() {
        // userListLoadingState.setValue(UserListLoadingState.loading);

        //get last local update date
        // Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("UserLastUpdateDate", 0);

        //firebase get all updates since lastLocalUpdateDate
        modelFirebase.getAllUsers(new ModelFirebase.GetAllUsersListener() {
            @Override
            public void onComplete(List<User> users) {
                //add all records to the local db
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long lud = new Long(0);
                        Log.d("TAG45", "fb returned " + users.size());
                        for (User user : users) {
                            Log.d("TAG455", "fb returned " + user.getConnected());
                            if (user.getConnected().equals("true")) {
                                Log.d("TAG100", "AppLocalDb.db.userDao() true: " + user.getName());
                                AppLocalDb.db.userDao().insertAll(user);
                                Log.d("TAG99", "AppLocalDb.db.userDao() true: " + AppLocalDb.db.userDao());
                            }
//                            if (lud < user.getUpdateDate()) {
//                                lud = user.getUpdateDate();
//                            }
                            //delete from room
//                            if(user.getConnected().equals("false")) {
//                                Log.d("TAG100", "AppLocalDb.db.userDao() true: ");
//                                Model.instance.getUserById(Model.instance.getConnectedUserId(), new Model.GetUserById() {
//                                    @Override
//                                    public void onComplete(User user) {
//                                        Log.d("TAG99", "user name: " + user.getName());
//                                        AppLocalDb.db.userDao().delete(user);
//                                    }
//                                });
//                                Log.d("TAG990", "AppLocalDb.db.userDao() false: " + AppLocalDb.db.userDao());
//                            }
                        }
                        //update last local update date
                        MyApplication.getContext()
                                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                                .edit()
                                // .putLong("UserLastUpdateDate", lud)
                                .commit();
                        //return all data to caller
//                        List<User> reList = AppLocalDb.db.userDao().getAll();
//                        usersList.postValue(reList);
//                        userListLoadingState.postValue(UserListLoadingState.loaded);
                    }
                });
            }
        });
    }

    public interface GetAllPostsListener{
        void onComplete(List<Post> list);
    }

    public void getAllPosts(GetAllPostsListener listener){
        executor.execute(()-> {
            List<Post> list = AppLocalDb.db.postDao().getAll();
            mainThread.post(()->{
                listener.onComplete(list);
            });
        });
    }

    public interface AddUserListener{
        void onComplete();
    }

    public void addUser(User user, AddUserListener listener){
        modelFirebase.addUser(user, ()->{
            listener.onComplete();
            refreshUserList();
        });
    }

    public interface LoginListener{
        void onComplete();
    }

    public void login(String email, String password, LoginListener listener) {
        executor.execute(()->{
            modelFirebase.login(email, password, ()->{
                listener.onComplete();
            });
//            mainThread.post(()->{
//                listener.onComplete();
//            });
        });

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Adding to room
        getUserById(getConnectedUserId(), new Model.GetUserById() {
            @Override
            public void onComplete(User user) {
                if(user.getConnected() == "false"){
                    user.setConnected("true");
                    editUser(user, new EditUserListener() {
                        @Override
                        public void onComplete() {
                            refreshUserList();
                        }
                    });
                }

            }
        });
    }

    public interface LogoutListener{
        void onComplete();
    }

    public void logout(LogoutListener listener){
//        getUserById(Model.instance.getConnectedUserId(), new Model.GetUserById() {
//            @Override
//            public void onComplete(User user) {
//                user.setConnected("false");
//            }
//        });
        //  user.setConnected(false);
        modelFirebase.logout(()-> {
            listener.onComplete();
           // refreshUserList();
        });

;
    }

    public interface AddPostListener{
        void onComplete();
    }

    public void addPost(Post post, AddPostListener listener){
        modelFirebase.addPost(post, listener);
    }

    public interface GetUserById{
        void onComplete(User user);
    }

    public User getUserById(String userId, GetUserById listener){
        modelFirebase.getUserById(userId, listener);
        return null;
    }

    public String getConnectedUserId(){
        return modelFirebase.getConnectedUserId();
    }

    public interface GetPostById {
        void onComplete(Post post);
    }
    public Post getPostById(String postId, GetPostById listener){
        modelFirebase.getPostById(postId, listener);
        return null;
    }

    public interface SaveImageListener{
        void onComplete(String url);
    }
    public void saveImage(Bitmap imageBitmap, String imageName, SaveImageListener listener) {
        modelFirebase.saveImage(imageBitmap,imageName,listener);
    }
    public interface EditUserListener {
        void onComplete();
    }

    public void editUser(User newUser, EditUserListener listener){
        modelFirebase.editUser(newUser, ()->{
            listener.onComplete();
        });
    }

//    public interface GetPostsByUserId {
//        void onComplete(List<Post> post);
//    }
//    public Post getPostByUserId(String userId){
//        modelFirebase.getPostsByUserId(postId, listener);
//        return null;
//    }
}
