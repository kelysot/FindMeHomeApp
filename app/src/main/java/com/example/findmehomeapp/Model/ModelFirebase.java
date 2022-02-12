package com.example.findmehomeapp.Model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModelFirebase {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();


    public ModelFirebase() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);
    }

    public void addUser(User user, Model.AddUserListener listener) {

        firebaseAuth.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    currentUser = firebaseAuth.getCurrentUser();
                    user.id = currentUser.getUid();
                    Log.d("TAG", "saved name:" + user.name + "user Id:" + user.id);

                    Map<String, Object> json = user.toJson();
                    db.collection(User.COLLECTION_NAME)
                            .document(user.getId())
                            .set(json)
                            .addOnSuccessListener(unused -> listener.onComplete(user))
                            .addOnFailureListener(e -> listener.onComplete(user));

                }
            }
        });
    }

    public void logout(Model.LogoutListener listener) {
        firebaseAuth.signOut();
        listener.onComplete();
    }

    public void login(String email, String password, Model.LoginListener listener) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser= firebaseAuth.getCurrentUser();
                            listener.onComplete();
                        }

                    }
                });
    }

    public void savePost(Post post, Model.UpdatePostListener listener) {
        Map<String, Object> json = post.toJson();
        db.collection(Post.COLLECTION_NAME)
                .document(post.getId())
                .set(json)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public interface GetAllPostsListener {
        void onComplete(List<Post> list);
    }

    public void getAllPosts(Long lastUpdateDate, GetAllPostsListener listener) {
        db.collection(Post.COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("updateDate",new Timestamp(lastUpdateDate,0))
                .get()
                .addOnCompleteListener(task -> {
                    List<Post> list = new LinkedList<Post>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Post post = Post.create(doc.getId(), doc.getData());
                            if (post != null) {
                                list.add(post);
                            }
                        }
                    }
                    listener.onComplete(list);
                });
    }

    public void getUserAllPosts(String userId, Long lastUpdateDate, GetAllPostsListener listener) {
        db.collection(Post.COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("updateDate",new Timestamp(lastUpdateDate,0))
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    List<Post> list = new LinkedList<Post>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Post post = Post.create(doc.getId(), doc.getData());
                            if (post != null) {
                                list.add(post);
                            }
                        }
                    }
                    listener.onComplete(list);
                });
    }

    public interface GetAllUsersListener {
        void onComplete(List<User> list);
    }

    public void getAllUsers(GetAllUsersListener listener) {
        db.collection(User.COLLECTION_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    List<User> list = new LinkedList<User>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            User user = User.create(doc.getData());
                            if (user != null) {
                                list.add(user);
                            }
                        }
                    }
                    listener.onComplete(list);
                });
    }

    public void addPost(Post post, Model.AddPostListener listener) {
        Map<String, Object> json = post.toJson();
        db.collection(Post.COLLECTION_NAME)
                .document()
                .set(json)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public void getPostById(String postId, Model.GetPostById listener) {
        db.collection(Post.COLLECTION_NAME)
                .document(postId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Post post = null;
                        if (task.isSuccessful() & task.getResult() != null) {
                            post = Post.create(postId, task.getResult().getData());
                        }
                        listener.onComplete(post);
                    }
                });
    }

    public void getUserById(String userId, Model.GetUserById listener) {
        db.collection(User.COLLECTION_NAME)
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user = null;
                        if (task.isSuccessful() & task.getResult() != null) {
                            user = User.create(task.getResult().getData());
                        }
                        listener.onComplete(user);
                    }
                });
    }
    public FirebaseUser getConnectedUser() {
        return firebaseAuth.getCurrentUser();
    }
    public String getConnectedUserId() {
        return currentUser.getUid();
    }

    public void getUserByEmail(String email, Model.GetUserByEmail listener) {
        //Map<String, Object> json = recipe.toJson();
        db.collection(User.COLLECTION_NAME)
                .whereEqualTo("email",email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() & task.getResult()!= null && task.getResult().getDocuments().size()!=0) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentId = documentSnapshot.getId();
                            db.collection(User.COLLECTION_NAME)
                                    .document(documentId).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            User user= null;
                                            if (task.isSuccessful() & task.getResult() != null) {
                                                user = User.create(task.getResult().getData());
                                                Log.d("TAG09", "login:" + user.getName());
                                                listener.onComplete(user);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public void editUser(User user, Model.EditUserListener listener) {
        Map<String, Object> json = user.toJson();
        db.collection(User.COLLECTION_NAME)
                .document(user.getId())
                .set(json)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    /**
     * Firebase Storage
     */

    public void saveImage(Bitmap imageBitmap, String imageName, Model.SaveImageListener listener) {
        StorageReference storageRef = storage.getReference();
        StorageReference imgRef = storageRef.child("user_avatars/" + imageName);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imgRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> listener.onComplete(null))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Uri downloadUrl = uri;
                            listener.onComplete(downloadUrl.toString());
                        });
                    }
                });
    }

    public void deleteImage(String imageName, Model.DeleteImageListener listener) {
        StorageReference storageRef = storage.getReference();
        StorageReference imgRef = storageRef.child("user_avatars/" + imageName);

        // Delete the file
        imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                listener.onComplete();
            }
        });
    }

//    public void getPostsByUserId(String userId, Model.GetPostsByUserId listener) {
//        db.collection(Post.COLLECTION_NAME)
//                .whereEqualTo("userId", userId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    List<Post> posts = new LinkedList<Post>();
//                    if (task.isSuccessful()){
//                        for (QueryDocumentSnapshot doc : task.getResult()){
//                            Post post = Post.create(doc.getData());
//                            if (posts != null){
//                                posts.add(post);
//                            }
//                        }
//                    }
//                    listener.onComplete(posts);
//                });
////                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
////                    @Override
////                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
////                        Post post = null;
////                        if (task.isSuccessful() & task.getResult()!= null) {
////                            post = Post.create(task.getResult().getData());
////                        }
////                        listener.onComplete(post);
////                    }
////                });
//    }
}
