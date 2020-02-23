package com.example.tutoresi.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.tutoresi.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class FirebaseSource {

    private FirebaseAuth mAuth;
    private DatabaseReference mDB;
    private static final String TAG = "FIREBASE_SOURCE";

    public FirebaseSource() {
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference();
        mDB = mDB.child("users");
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
                        mDB.child(mAuth.getCurrentUser().getUid()).setValue(user);
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

    public FirebaseUser currentUser() {
        return mAuth.getCurrentUser();
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
            mDB.child(mAuth.getCurrentUser().getUid());
            mDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // exist
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // not exit yet
                    mDB.setValue(new User(mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getEmail(), ""));
                }
            });
        }
    }
}

