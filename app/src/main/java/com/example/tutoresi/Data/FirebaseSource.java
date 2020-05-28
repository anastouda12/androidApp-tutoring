package com.example.tutoresi.Data;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.tutoresi.Config.ErrorsCode;
import com.example.tutoresi.Model.Course;
import com.example.tutoresi.Model.Rating;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
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

    public FirebaseUser getCurrentFirebaseUser(){
        return mAuth.getCurrentUser();
    }

    public void uploadProfileImageCurrentUser(Uri uri){
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

    public MutableLiveData<Uri> getAvatarProfileOfUser(String email){
        final MutableLiveData<Uri> imageUri = new MutableLiveData<>();
        DatabaseReference ref = mDB.child("users");
        ref.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
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
                Log.d(TAG,databaseError.getMessage());
            }
        });
        return imageUri;
    }


    public MutableLiveData<Boolean> login(String email, String password) {
        final MutableLiveData<Boolean> authenticatedUserMutableLiveData = new MutableLiveData<>();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                        authenticatedUserMutableLiveData.setValue(true);
                } else {
                    Log.d(TAG, "signInWithEmail:failed");
                        authenticatedUserMutableLiveData.setValue(false);
                    }
            }
        });
        return  authenticatedUserMutableLiveData;
    }

    public MutableLiveData<Integer> register(final String email, final String password, final String name, final String phone) {
        final MutableLiveData<Integer> authenticatedUserMutableLiveData = new MutableLiveData<>();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "RegisterWithEmailPassword:success");
                        User user = new User(name, email, phone);
                        mDB.child("users").child(mAuth.getCurrentUser().getUid()).setValue(user); // create user in DB
                        authenticatedUserMutableLiveData.setValue(ErrorsCode.SUCCESS_REGISTER);
                } else {
                    // sign in fails
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthWeakPasswordException e) {
                        authenticatedUserMutableLiveData.setValue(ErrorsCode.ERROR_WEAK_PASSWORD);
                    } catch(FirebaseAuthInvalidCredentialsException e) {
                        authenticatedUserMutableLiveData.setValue(ErrorsCode.ERROR_INVALID_EMAIL);
                    } catch(FirebaseAuthUserCollisionException e) {
                        authenticatedUserMutableLiveData.setValue(ErrorsCode.ERROR_USER_EXISTS);
                    } catch(Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
        }});
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
                Log.d(TAG,databaseError.getMessage());
            }
        });
        return user;
    }

    public void updateDataUser(String name, String phone){
        mDB.child("users").child(mAuth.getCurrentUser().getUid()).child("name").setValue(name);
        mDB.child("users").child(mAuth.getCurrentUser().getUid()).child("phone").setValue(phone);
    }


    public MutableLiveData<Boolean> firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        final MutableLiveData<Boolean> authenticatedUserMutableLiveData = new MutableLiveData<>();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                                createUserIfNotExistInDB(); // optional
                                authenticatedUserMutableLiveData.setValue(true);
                        } else {
                            // sign in fails
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            authenticatedUserMutableLiveData.setValue(false);
                        }

                    }
                });
        return authenticatedUserMutableLiveData;
    }

    /**
     * Create structure User of the currentUser connected if not exist (in the case of google sign in)
     */
    private void createUserIfNotExistInDB() {
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
                    Log.d(TAG,databaseError.getMessage());
                }
            });
        }
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
                Log.d(TAG,databaseError.getMessage());
            }
        });

    }

    public void addTutoring(final String courseId, final String descriptionTutoring){
        DatabaseReference refCourses = mDB.child("courses").child(courseId).child("tutoring");
        refCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference ref = mDB.child("users").child(mAuth.getCurrentUser().getUid());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mDB.child("courses").child(courseId).child("tutoring").child(mAuth.getCurrentUser().getUid()).setValue(new Tutoring
                                (new User(dataSnapshot.child("name").getValue().toString(), dataSnapshot.child("email").getValue().toString(),dataSnapshot.child("phone").getValue().toString())
                                        ,descriptionTutoring));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG,databaseError.getMessage());
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,databaseError.getMessage());
            }
        });
    }

    public MutableLiveData<Boolean> courseHasTutors(String courseId){
        DatabaseReference ref = mDB.child("courses").child(courseId);
        final MutableLiveData<Boolean> hasChild = new MutableLiveData<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("tutors")){
                    // has tutors
                    hasChild.setValue(true);
                }else{
                    hasChild.setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,databaseError.getMessage());
            }
        });
        return hasChild;
    }

    public void removeCourse(String courseId){
         mDB.child("courses").child(courseId).removeValue();
    }

    public void rateUser(String userEmail, final Rating rate){
            final DatabaseReference ref = mDB.child("users");
            ref.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final String key = dataSnapshot.getChildren().iterator().next().getKey();
                        ref.child(key).child("ratings").child(mAuth.getCurrentUser().getUid()).setValue(rate);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage());
                }
            });

    }

    public MutableLiveData<Rating> getRatingOfUser(String userEmail){
        final MutableLiveData<Rating> rating = new MutableLiveData<>();
        final DatabaseReference ref = mDB.child("users");
        ref.orderByChild("email").equalTo(userEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    final String key = dataSnapshot.getChildren().iterator().next().getKey();
                    ref.child(key).child("ratings").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            float total = 0;
                            int nbRatings = 0;
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                total += ds.getValue(Rating.class).getRate();
                                nbRatings++;
                            }
                            rating.setValue(new Rating(total/nbRatings));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG,databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,databaseError.getMessage());
            }
        });
        return rating;
    }
}



