package com.example.tutoresi.Data;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.tutoresi.Model.Course;
import com.example.tutoresi.Model.Reminder;
import com.example.tutoresi.Model.Tutoring;
import com.example.tutoresi.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class FirebaseSource {

    private FirebaseAuth mAuth;
    private DatabaseReference mDB;
    private StorageReference mStore;
    private static final String TAG = "FIREBASE_SOURCE";

    public FirebaseSource() {
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseStorage.getInstance().getReference("Images");
        mDB = FirebaseDatabase.getInstance().getReference();
    }


    public void uploadProfilImageCurrentUser(Uri uri){
        StorageReference ref = mStore.child(mAuth.getCurrentUser().getUid()).child("profileImage");
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               // Uri downloadUrl = taskSnapshot.getUploadSessionUri(); url of the upload content
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // handle failure here
            }
        });
    }

    public MutableLiveData<Uri> getAvatarProfileCurrentUser(){
        final MutableLiveData<Uri> imageUri = new MutableLiveData<>();
        StorageReference ref = mStore.child(mAuth.getCurrentUser().getUid()).child("profileImage");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageUri.setValue(uri);
           }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //
            }
        });
        return imageUri;
    }

    public MutableLiveData<Uri> getAvatarProfileOfUser(final User user){
        final MutableLiveData<Uri> imageUri = new MutableLiveData<>();
        DatabaseReference ref = mDB.child("users");
        ref.orderByChild("email").equalTo(user.getEmail()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String key = dataSnapshot.getChildren().iterator().next().getKey();
                    StorageReference ref = mStore.child(key).child("profileImage");
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUri.setValue(uri);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("no-image");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return imageUri;
    }


    public MutableLiveData<User> login(String email, String password) {
        final MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        User user = new User(firebaseUser.getDisplayName(), firebaseUser.getEmail(), "");
                        authenticatedUserMutableLiveData.setValue(user);
                } else {
                    Log.d(TAG, "signInWithEmail:failed");
                }
            }
        }});
        return  authenticatedUserMutableLiveData;
    }

    public MutableLiveData<User> register(final String email, final String password, final String name, final String phone) {
        final MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "RegisterWithEmailPassword:success");
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        User user = new User(name, email, phone);
                        mDB.child("users").child(mAuth.getCurrentUser().getUid()).setValue(user);
                        authenticatedUserMutableLiveData.setValue(user);
                    }
                } else {
                    Log.d(TAG, "RegisterWithEmailPassword:failed");
                }
            }
        });

        return authenticatedUserMutableLiveData;
    }

    public void logout() {
            mAuth.signOut();
    }

    public MutableLiveData<User> currentUser() {
        final MutableLiveData<User> user = new MutableLiveData<>();
        DatabaseReference ref = mDB.child("users").child(mAuth.getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user.setValue(new User(dataSnapshot.child("name").getValue().toString(), dataSnapshot.child("email").getValue().toString(),dataSnapshot.child("phone").getValue().toString()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return user;
    }

    public void updateDataUser(String name, String phone){
        mDB.child("users").child(mAuth.getCurrentUser().getUid()).child("name").setValue(name);
        mDB.child("users").child(mAuth.getCurrentUser().getUid()).child("phone").setValue(phone);
    }


    public MutableLiveData<User> firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        final MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                createUser(); // optional
                                authenticatedUserMutableLiveData.setValue(new User(mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getEmail(), ""));
                            }
                        } else {
                            // sign in fails
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                    }
                });
        return authenticatedUserMutableLiveData;
    }

    /**
     * Create structure User of the currentUser connected if not exist (in the case of google sign in)
     */
    private void createUser() {
        if (mAuth.getCurrentUser() != null) {
            DatabaseReference ref = mDB.child("users").child(mAuth.getCurrentUser().getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        // not exit yet
                       mDB.child("users").child(mAuth.getCurrentUser().getUid()).setValue(new User(mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getEmail(), ""));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    /**
     * Get reminder of the current User connected
     * @return
     */
    public MutableLiveData<List<Reminder>> getMyReminders(){
        final MutableLiveData<List<Reminder>> rem = new MutableLiveData<>();
        List<Reminder> list = new ArrayList<>();
        rem.setValue(list);
        DatabaseReference refReminders = mDB.child("users").child(mAuth.getCurrentUser().getUid()).child("reminders");
        refReminders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rem.getValue().add(dataSnapshot.getValue(Reminder.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return rem;
    }

    /**
     * Get reminder of the current User connected
     * @return
     */
    public MutableLiveData<List<Course>> getMyCourses(){
        final MutableLiveData<List<Course>> rem = new MutableLiveData<>();
        List<Course> list = new ArrayList<>();
        rem.setValue(list);
        DatabaseReference refCourses = mDB.child("courses");
        refCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rem.getValue().add(dataSnapshot.getValue(Course.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return rem;
    }

    /**
     * Add reminder to the currentUser
     * @param reminder
     */
    public void addReminder(Reminder reminder){
        DatabaseReference refReminders = mDB.child("users").child(mAuth.getCurrentUser().getUid()).child("reminders");
        refReminders.push().setValue(reminder);
    }

    /**
     * Add new course of tutoring
     * @param course course of tutoring
     */
    public void addCourse(final Course course){
        DatabaseReference refCourses = mDB.child("courses").child(course.getId());
        refCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    // not exist yet
                    mDB.child("courses").child(course.getId()).setValue(course);
                }else{
                    // course already exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void addTutoring(final Course course, final String descriptionTutoring){
        DatabaseReference refCourses = mDB.child("courses").child(course.getId()).child("tutoring");
        refCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference ref = mDB.child("users").child(mAuth.getCurrentUser().getUid());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mDB.child("courses").child(course.getId()).child("tutoring").child(mAuth.getCurrentUser().getUid()).setValue(new Tutoring
                                (new User(dataSnapshot.child("name").getValue().toString(), dataSnapshot.child("email").getValue().toString(),dataSnapshot.child("phone").getValue().toString())
                                        ,descriptionTutoring));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public MutableLiveData<Boolean> checksAvailabilityTutoringCourse(Course course){
        final MutableLiveData<Boolean> avail = new MutableLiveData<>();
        DatabaseReference ref = mDB.child("courses").child(course.getId()).child("tutoring");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    //no tutoring
                    avail.setValue(false);
                }else{
                    avail.setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return avail;
    }
}



