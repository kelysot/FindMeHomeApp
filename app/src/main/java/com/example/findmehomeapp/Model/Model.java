package com.example.findmehomeapp.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.findmehomeapp.MyApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Model {
    public static final Model instance = new Model();

    Executor executor = Executors.newFixedThreadPool(1);
    Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());
    FirebaseAuth firebaseAuth;

    public enum PostListLoadingState {
        loading,
        loaded
    }
    MutableLiveData<PostListLoadingState> postListLoadingState = new MutableLiveData<PostListLoadingState>();
    public LiveData<PostListLoadingState> getPostListLoadingState() {
        return postListLoadingState;
    }

    ModelFirebase modelFirebase = new ModelFirebase();

    private Model() {
        postListLoadingState.setValue(PostListLoadingState.loaded);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    MutableLiveData<List<Post>> postsList = new MutableLiveData<List<Post>>();
    MutableLiveData<List<Post>> userPostsList = new MutableLiveData<List<Post>>();
    MutableLiveData<List<User>> usersList = new MutableLiveData<List<User>>();

    public LiveData<List<Post>> getAllPosts() {
        if (postsList.getValue() == null) {
            refreshPostsList();
        }
        return postsList;
    }

    public LiveData<List<Post>> getAllUserPosts() {
        if (userPostsList.getValue() == null) {
            refreshUserPostsList();
        }
        return userPostsList;
    }

    public void refreshPostsList() {
        postListLoadingState.setValue(PostListLoadingState.loading);

        // get last local update date
        Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("PostsLastUpdateDate",0);

        // firebase get all updates since lastLocalUpdateDate
        modelFirebase.getAllPosts(lastUpdateDate, new ModelFirebase.GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> list) {
                // add all records to the local db
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long lud = new Long(0);
                        // TODO: delete this row
//                        AppLocalDb.db.postDao().deleteAll();
                        Log.d("TAG", "fb returned " + list.size());
                        for (Post post : list) {
                            AppLocalDb.db.postDao().insertAll(post);
                            if (lud < post.getUpdateDate()){
                                lud = post.getUpdateDate();
                            }
                        }
                        // update last local update date
                        MyApplication.getContext()
                                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                                .edit()
                                .putLong("PostsLastUpdateDate",lud)
                                .commit();

                        //return all data to caller
                        List<Post> stList = AppLocalDb.db.postDao().getAll();

                        Comparator<Post> compareByUpdateTime =
                                (Post o1, Post o2) -> o1.getUpdateDate().compareTo( o2.getUpdateDate() );

                        Collections.sort(stList, compareByUpdateTime);
                        Collections.reverse(stList);

                        postsList.postValue(stList);

                        postListLoadingState.postValue(PostListLoadingState.loaded);
                    }
                });
            }
        });
    }

    public void refreshUserPostsList() {
        postListLoadingState.setValue(PostListLoadingState.loading);

        // get last local update date
        Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("PostsLastUpdateDate",0);
        String userId = firebaseAuth.getCurrentUser().getUid();

        // firebase get all updates since lastLocalUpdateDate
        modelFirebase.getAllPosts(lastUpdateDate, new ModelFirebase.GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> list) {
                // add all records to the local db
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long lud = new Long(0);
                        // TODO: delete this row
//                        AppLocalDb.db.postDao().deleteAll();
                        Log.d("TAG", "fb returned " + list.size());
                        for (Post post : list) {
                            AppLocalDb.db.postDao().insertAll(post);
                            if (lud < post.getUpdateDate()){
                                lud = post.getUpdateDate();
                            }
                        }
                        // update last local update date
                        MyApplication.getContext()
                                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                                .edit()
                                .putLong("PostsLastUpdateDate",lud)
                                .commit();

                        //return all data to caller
                        List<Post> stList = AppLocalDb.db.postDao().getUserAll(userId);

                        Comparator<Post> compareByUpdateTime =
                                (Post o1, Post o2) -> o1.getUpdateDate().compareTo( o2.getUpdateDate() );

                        Collections.sort(stList, compareByUpdateTime);
                        Collections.reverse(stList);

                        userPostsList.postValue(stList);

                        postListLoadingState.postValue(PostListLoadingState.loaded);
                    }
                });
            }
        });
    }

    public void refreshUserList() {
        Log.d("TAG", "fb returnedq111111111 ");

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
                        //      Long lud = new Long(0);
                        for (User user : users) {
                            if(user.getConnected().equals("true")){
                                List<User> daoUsers = AppLocalDb.db.userDao().getAll();
                                int daoSize = daoUsers.size();
                                if(daoSize != 0){
                                    for(int i = 0; i < daoSize; i ++){
                                        if (!user.getId().equals(daoUsers.get(i).getId())){
                                            if(i == (daoSize - 1)){
                                                AppLocalDb.db.userDao().insertAll(user);
                                            }
                                        }
                                    }
                                }
                                else{
                                    AppLocalDb.db.userDao().insertAll(user);
                                }

                            }

//                            if (user.getConnected().equals("true")) {
//                            }
//                            if (lud < user.getUpdateDate()) {
//                                lud = user.getUpdateDate();
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

    public interface AddUserListener {
        void onComplete(User user);
        void onFailure();
    }

    public void addUser(User user, AddUserListener listener) {
        modelFirebase.addUser(user, new AddUserListener() {
            @Override
            public void onComplete(User user) {
                listener.onComplete(user);
            }
            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });

        refreshUserList();
    }

    public interface LoginListener {
        void onComplete();
        void onFailure();
    }

    public void login(String email, String password, LoginListener listener) {
        modelFirebase.login(email, password, new LoginListener() {
            @Override
            public void onComplete() {
                getUserByEmail(email, new Model.GetUserByEmail() {
                    @Override
                    public void onComplete(User user) {
                        //   Log.d("TAG01", "login:");
                        if (user.getConnected().equals("false")) {
                            user.setConnected("true");
                            Model.instance.editUser(user, new EditUserListener() {
                                @Override
                                public void onComplete() {
                                    refreshUserList();

                                    listener.onComplete();
                                }

                                @Override
                                public void onFailure() {

                                }
                            });
                        }
                    }
                });
            }
            @Override
            public void onFailure() {
                listener.onFailure();
            }
        } );
            // Log.d("TAG00", "login:" + email);

    }

    public interface LogoutListener {
        void onComplete();
    }

    public void logout(LogoutListener listener) {
        modelFirebase.logout(listener);

    }

    public interface AddPostListener {
        void onComplete();
    }

    public void addPost(Post post, AddPostListener listener) {
        modelFirebase.addPost(post, listener);
    }

    public interface UpdatePostListener {
        void onComplete();
    }

    public void savePost(Post post, UpdatePostListener listener) {
        modelFirebase.savePost(post, listener);
    }

    public interface DeletePostListener {
        void onComplete();
    }

    public void deletePost(Post post, DeletePostListener listener) {
        modelFirebase.deletePost(post, new ModelFirebase.DeletePostListener() {
            @Override
            public void onComplete() {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        AppLocalDb.db.postDao().delete(post);
                    }
                });
                listener.onComplete();
            }
        });
    }

    public interface GetUserById {
        void onComplete(User user);
    }

    public User getUserById(String userId, GetUserById listener) {
        modelFirebase.getUserById(userId, listener);
        return null;
    }

    public FirebaseUser getConnectedUser() {
        return modelFirebase.getConnectedUser();
    }

    public interface GetUserByEmail {
        void onComplete(User user);
    }

    public User getUserByEmail(String email, GetUserByEmail listener) {
        modelFirebase.getUserByEmail(email, listener);
        return null;
    }

    public String getConnectedUserId() {
        return modelFirebase.getConnectedUserId();
    }

    public interface GetPostById {
        void onComplete(Post post);
    }

    public Post getPostById(String postId, GetPostById listener) {
        modelFirebase.getPostById(postId, listener);
        return null;
    }

    public interface SaveImageListener {
        void onComplete(String url);
    }

    public  void saveImage(Bitmap imageBitmap, String imageName, SaveImageListener listener) {
        modelFirebase.saveImage(imageBitmap, imageName, listener);
    }

    public interface DeleteImageListener {
        void onComplete();
    }

    public  void deleteImage(String imageName, DeleteImageListener listener) {
        modelFirebase.deleteImage(imageName, listener);
    }

    public interface EditUserListener {
        void onComplete();
        void onFailure();
    }

    public void editUser(User newUser, EditUserListener listener) {
        modelFirebase.editUser(newUser, new EditUserListener() {
            @Override
            public void onComplete() {
                listener.onComplete();
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
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
