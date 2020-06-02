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

/**
 * Firebase Source
 * Manages the data with the Database
 */
public class FirebaseSource {

    private FirebaseAuth mAuth;
    private DatabaseReference mDB;
    private StorageReference mStore;
    private static final String TAG = "FIREBASE_SOURCE";

    /**
     * Constructor of FirebaseSource
     */
    public FirebaseSource() {
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseStorage.getInstance().getReference("Images");
        mDB = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Get the current firebaseUser connected in the application.
     * @return FirebaseUser connected
     */
    public FirebaseUser getCurrentFirebaseUser(){
        return mAuth.getCurrentUser();
    }

    /**
     * Store the profile image of the currentuser connected in the application
     * @param uri the uri of the image to store.
     */
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

    /**
     * Get the avatarProfile of the currentuser connected
     * @return the URI of the image.
     */
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

    /**
     * Get the avatarProfile of the specified email user.
     * @param email the email of the user we want the avatar.
     * @return the URI of the image.
     */
    public MutableLiveData<Uri> getAvatarProfileOfUser(String email){
        final MutableLiveData<Uri> imageUri = new MutableLiveData<>();
        DatabaseReference ref = mDB.child("users");
        ref.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
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


    /**
     * Login an user with email and password.
     * @param email email
     * @param password password
     * @return true if connected successful
     */
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

    /**
     * Register an user in the application
     * @param email email
     * @param password password
     * @param name name
     * @param phone phone
     * @return code representing the result of the registering
     * ( CODE 0 : SUCCESS, CODE - 1 : WEAK PASSWORD, CODE -2 : INVALID MAIL, CODE -3 : USERALREADY EXiST)
     */
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

    /**
     * Logout
     */
    public void logout() {
            mAuth.signOut();
    }

    /**
     * Get the current user on object User(Name,Email,Phone)
     * @return object User with informations of user connected
     */
    public MutableLiveData<User> currentUser() {
        final MutableLiveData<User> user = new MutableLiveData<>();
        DatabaseReference ref = mDB.child("users").child(mAuth.getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    user.setValue(new User(dataSnapshot.child("name").getValue().toString(),
                            dataSnapshot.child("email").getValue().toString(),
                            dataSnapshot.child("phone").getValue().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,databaseError.getMessage());
            }
        });
        return user;
    }

    /**
     * Modify data of the user connected.
     * @param name name to modify
     * @param phone phone to modify
     */
    public void updateDataUser(final String name, final String phone){
        final DatabaseReference refUser = mDB.child("users").child(mAuth.getCurrentUser().getUid());
        refUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    refUser.child("name").setValue(name);
                    refUser.child("phone").setValue(phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,databaseError.getMessage());
            }
        });
    }


    /**
     * Authentification with google
     * @param acct GoogleSigninaccount
     * @return true if the connection was successfull
     */
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
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
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
     * @param reminder reminder to add
     */
    public void addReminder(final Reminder reminder){
        final DatabaseReference refReminders = mDB.child("users").child(mAuth.getCurrentUser().getUid()).child("reminders");
        refReminders.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                refReminders.push().setValue(reminder);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,databaseError.getMessage());
            }
        });
    }

    /**
     * Add new course of tutoring
     * @param course course of tutoring
     */
    public void addCourse(final Course course){
        DatabaseReference refCourses = mDB.child("courses").child(course.getId());
        refCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    // not exist yet
                    mDB.child("courses").child(course.getId()).setValue(course);
                }
                    //else
                    // course already exist
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,databaseError.getMessage());
            }
        });

    }

    /**
     * Register a tutoring of the current user connected in the specified course
     * @param courseId course to register inside the tutoring
     * @param descriptionTutoring description of the tutoring.
     */
    public void addTutoring(final String courseId, final String descriptionTutoring){
        final DatabaseReference refCourses = mDB.child("courses").child(courseId).child("tutoring").child(mAuth.getCurrentUser().getUid());
        refCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference ref = mDB.child("users").child(mAuth.getCurrentUser().getUid());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            refCourses.setValue(
                                    new Tutoring(
                                            new User(
                                                    dataSnapshot.child("name").getValue().toString(),
                                                    dataSnapshot.child("email").getValue().toString(),
                                                    dataSnapshot.child("phone").getValue().toString())
                                            , descriptionTutoring));
                        }
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

    /**
     * Checks if the course has tutors
     * @param courseId course to checks
     * @return true if the course has tutors
     */
    public MutableLiveData<Boolean> courseHasTutors(String courseId){
        DatabaseReference ref = mDB.child("courses").child(courseId);
        final MutableLiveData<Boolean> hasChild = new MutableLiveData<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("tutoring")){
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

    /**
     * Remove a course.
     * @param courseId course to remove.
     */
    public void removeCourse(String courseId){
        final DatabaseReference ref = mDB.child("courses").child(courseId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ref.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    /**
     * Note an user, the current user connected make the rate (1 rate possible)
     * @param userEmail user to rate
     * @param rate rate
     */
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

    /**
     * Get the average rating of the user given.
     * @param userEmail user to get the rating.
     * @return the rating
     */
    public MutableLiveData<Rating> getRatingOfUser(String userEmail){
        final MutableLiveData<Rating> rating = new MutableLiveData<>();
        final DatabaseReference ref = mDB.child("users");
        ref.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
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



