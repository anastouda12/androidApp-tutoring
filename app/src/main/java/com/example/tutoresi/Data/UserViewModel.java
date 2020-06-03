package com.example.tutoresi.Data;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.tutoresi.Model.Rating;
import com.example.tutoresi.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;


public class UserViewModel extends ViewModel {

    private UserRepository userRepository;

    public UserViewModel(){
        userRepository = UserRepository.getInstance();
    }

    public LiveData<Integer> register(String email, String password, String name, String phone){
       return userRepository.register(email,password,name,phone);
    }

    public LiveData<Boolean> login(String email, String password){
        return userRepository.login(email,password);
    }

    public LiveData<Boolean> signInWithGoogle(GoogleSignInAccount acct){
        return userRepository.loginWithGoogle(acct);
    }

    public void signOut(){
        userRepository.logout();
    }

    public LiveData<User> currentUser(){
        return userRepository.currentUser();
    }

    public FirebaseUser getCurrentFirebaseUser(){
        return userRepository.getCurrentFirebaseUser();
    }

    public LiveData<Boolean> uploadProfileImageCurrentUser(Uri uri){
         return userRepository.uploadProfileImageCurrentUser(uri);
    }

    public LiveData<Uri> getProfileImageCurrentUser(){
        return userRepository.getProfileImageCurrentUser();
    }

    public LiveData<Uri> getProfileImageOfUser(String email){
        return userRepository.getProfileImageOfUser(email);
    }

    public LiveData<Boolean> updateDataUser(String name, String phone){
        return userRepository.updateDataUser(name,phone);
    }

    public LiveData<Boolean> rateUser(String userEmail, Rating rate){
        return userRepository.rateUser(userEmail,rate);
    }

    public LiveData<Rating> getRatingOfUser(String userEmail){
        return userRepository.getRatingOfUser(userEmail);
    }

}
