package com.example.tutoresi.Data;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import com.example.tutoresi.Model.Rating;
import com.example.tutoresi.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

public class UserRepository {

    private FirebaseSource firebaseSource;


    public UserRepository(){
        firebaseSource = new FirebaseSource();
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
    public MutableLiveData<Boolean> register(String email, String password, String name, String phone){
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

    public void uploadeImageProfileCurrentUser(Uri uri){firebaseSource.uploadProfilImageCurrentUser(uri);}

    public MutableLiveData<Uri> getProfileImageCurrentUser(){
        return firebaseSource.getAvatarProfileCurrentUser();
    }

    public MutableLiveData<Uri> getProfileImageOfUser(User user){
        return firebaseSource.getAvatarProfileOfUser(user);
    }

    public void updateDataUser(String name, String phone){
        firebaseSource.updateDataUser(name,phone);
    }

    public void rateUser(String userEmail, Rating rate){
        firebaseSource.rateUser(userEmail,rate);
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
