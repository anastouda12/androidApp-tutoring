package com.example.tutoresi.Data;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import com.example.tutoresi.Model.Rating;
import com.example.tutoresi.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

public class UserRepository {

    private FirebaseSource firebaseSource;
    private static UserRepository instance;

    private UserRepository(){
        firebaseSource = FirebaseSource.getInstance();
    }

    /**
     * Get an instance of userRepository
     * @return instance of userRepository
     */
    public static UserRepository getInstance(){
        if(instance == null){
            instance = new UserRepository();
        }
        return instance;
    }

    public MutableLiveData<Boolean> login(String email, String password){
        return firebaseSource.login(email,password);
    }

    /**
     * Create account auth of user and create datastructure in DB
     * @param email email
     * @param password password
     * @param name name
     * @param phone phone
     */
    public MutableLiveData<Integer> register(String email, String password, String name, String phone){
        return firebaseSource.register(email, password,name, phone);
    }

    public MutableLiveData<User> currentUser(){
        return firebaseSource.currentUser();
    }

    public FirebaseUser getCurrentFirebaseUser(){
        return firebaseSource.getCurrentFirebaseUser();
    }


    public void logout(){
        firebaseSource.logout();
    }

    public MutableLiveData<Boolean> uploadProfileImageCurrentUser(Uri uri){return firebaseSource.uploadProfileImageCurrentUser(uri);}

    public MutableLiveData<Uri> getProfileImageCurrentUser(){
        return firebaseSource.getAvatarProfileCurrentUser();
    }

    public MutableLiveData<Uri> getProfileImageOfUser(String email){
        return firebaseSource.getAvatarProfileOfUser(email);
    }

    public MutableLiveData<Boolean> updateDataUser(String name, String phone){
        return firebaseSource.updateDataUser(name,phone);
    }

    public MutableLiveData<Boolean> rateUser(String userEmail, Rating rate){
        return firebaseSource.rateUser(userEmail,rate);
    }

    public  MutableLiveData<Rating> getRatingOfUser(String userEmail){
        return firebaseSource.getRatingOfUser(userEmail);
    }

    /**
     * Sign in with google account
     * @param googleSignInAccount google account
     */
    public MutableLiveData<Boolean> loginWithGoogle(GoogleSignInAccount googleSignInAccount){
        return firebaseSource.firebaseAuthWithGoogle(googleSignInAccount);
    }

}
