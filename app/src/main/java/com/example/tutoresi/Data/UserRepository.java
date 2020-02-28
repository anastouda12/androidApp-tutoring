package com.example.tutoresi.Data;

import androidx.lifecycle.MutableLiveData;

import com.example.tutoresi.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

public class UserRepository {

    private FirebaseSource firebaseSource;


    public UserRepository(){
        firebaseSource = new FirebaseSource();
    }

    public MutableLiveData<User> login(String email, String password){
        return firebaseSource.login(email,password);
    }

    /**
     * Create account auth of user and create datastructure in DB
     * @param email email
     * @param password password
     * @param name name
     * @param phone phone
     */
    public MutableLiveData<User> register(String email, String password, String name, String phone){
        return firebaseSource.register(email, password,name, phone);
    }

    public FirebaseUser currentUser(){
        return firebaseSource.currentUser();
    }

    public void logout(){
        firebaseSource.logout();
    }


    /**
     * Sign in with google account
     * @param googleSignInAccount google account
     */
    public MutableLiveData<User> loginWithGoogle(GoogleSignInAccount googleSignInAccount){
        return firebaseSource.firebaseAuthWithGoogle(googleSignInAccount);
    }

}
