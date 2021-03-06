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

    public Executor executor = Executors.newFixedThreadPool(1);

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
    }

    MutableLiveData<List<Post>> postsList = new MutableLiveData<List<Post>>();
    MutableLiveData<List<Post>> userPostsList = new MutableLiveData<List<Post>>();

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
        Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("PostsLastUpdateDate", 0);

        // firebase get all updates since lastLocalUpdateDate
        modelFirebase.getAllPosts(lastUpdateDate, new ModelFirebase.GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> list) {
                // add all records to the local db
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long lud = new Long(0);
                        for (Post post : list) {
                            if (post.isDeleted) {
                                AppLocalDb.db.postDao().deleteById(post.getId());
                            } else {
                                AppLocalDb.db.postDao().insertAll(post);
                            }
                            if (lud < post.getUpdateDate()) {
                                lud = post.getUpdateDate();
                            }
                        }
                        // update last local update date
                        MyApplication.getContext()
                                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                                .edit()
                                .putLong("PostsLastUpdateDate", lud)
                                .commit();

                        //return all data to caller
                        List<Post> stList = AppLocalDb.db.postDao().getAll();

                        Comparator<Post> compareByUpdateTime =
                                (Post o1, Post o2) -> o1.getUpdateDate().compareTo(o2.getUpdateDate());

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
        Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("PostsLastUpdateDate", 0);
        String userId = getConnectedUserId();

        // firebase get all updates since lastLocalUpdateDate
        modelFirebase.getAllPosts(lastUpdateDate, new ModelFirebase.GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> list) {
                // add all records to the local db
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long lud = new Long(0);
                        for (Post post : list) {
                            if (post.isDeleted) {
                                AppLocalDb.db.postDao().deleteById(post.getId());
                            } else {
                                AppLocalDb.db.postDao().insertAll(post);
                            }
                            if (lud < post.getUpdateDate()) {
                                lud = post.getUpdateDate();
                            }
                        }
                        // update last local update date
                        MyApplication.getContext()
                                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                                .edit()
                                .putLong("PostsLastUpdateDate", lud)
                                .commit();

                        //return all data to caller
                        List<Post> stList = AppLocalDb.db.postDao().getUserAll(userId);

                        Comparator<Post> compareByUpdateTime =
                                (Post o1, Post o2) -> o1.getUpdateDate().compareTo(o2.getUpdateDate());

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
        modelFirebase.getAllUsers(new ModelFirebase.GetAllUsersListener() {
            @Override
            public void onComplete(List<User> users) {
                //add all records to the local db
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        //      Long lud = new Long(0);
                        for (User user : users) {
                            if (user.getConnected().equals("true")) {
                                List<User> daoUsers = AppLocalDb.db.userDao().getAll();
                                int daoSize = daoUsers.size();
                                if (daoSize != 0) {
                                    for (int i = 0; i < daoSize; i++) {
                                        if (!user.getId().equals(daoUsers.get(i).getId())) {
                                            if (i == (daoSize - 1)) {
                                                AppLocalDb.db.userDao().insertAll(user);
                                            }
                                        }
                                    }
                                } else {
                                    AppLocalDb.db.userDao().insertAll(user);
                                }

                            }
                        }
                        //update last local update date
                        MyApplication.getContext()
                                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                                .edit()
                                .commit();
                    }
                });
            }
        });
    }

    public interface AddUserListener {
        void onComplete(User user);

        void onFailure();
    }

    public void addUser(User user, String password, AddUserListener listener) {
        modelFirebase.addUser(user, password, new AddUserListener() {
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
                        if (user.getConnected().equals("false")) {
                            user.setConnected("true");
                        }
                        Model.instance.editUser(user, new EditUserListener() {
                            @Override
                            public void onComplete() {
                                refreshUserList();

                                listener.onComplete();
                            }

                            @Override
                            public void onFailure() {
                                listener.onFailure();
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });
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

        void onFailure();
    }

    public void savePost(Post post, UpdatePostListener listener) {
        modelFirebase.savePost(post, new UpdatePostListener() {
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

    public void deletePost(Post post, UpdatePostListener listener) {
        modelFirebase.savePost(post, new UpdatePostListener() {
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

            @Override
            public void onFailure() {
                listener.onFailure();
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

    public void saveImage(Bitmap imageBitmap, String imageName, SaveImageListener listener) {
        modelFirebase.saveImage(imageBitmap, imageName, listener);
    }

    public interface SavePostImageListener {
        void onComplete(String url);
    }

    public void savePostImage(Bitmap imageBitmap, String imageName, SavePostImageListener listener) {
        modelFirebase.savePostImage(imageBitmap, imageName, listener);
    }

    public interface DeleteImagePostListener {
        void onComplete();
    }

    public void deleteImagePost(String imageName, DeleteImagePostListener listener) {
        modelFirebase.deleteImagePost(imageName, listener);
    }

    public interface DeleteImageListener {
        void onComplete();
    }

    public void deleteImage(String imageName, DeleteImageListener listener) {
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

}
