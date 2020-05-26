package com.example.tutoresi.Data;

import android.net.Uri;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tutoresi.Model.Rating;
import com.example.tutoresi.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;


public class UserViewModel extends ViewModel {

    private UserRepository userRepository;

    public UserViewModel(){
        userRepository = new UserRepository();
    }

    public MutableLiveData<Boolean> register(String email, String password, String name, String phone){
       return userRepository.register(email,password,name,phone);
    }

    public MutableLiveData<Boolean> login(String email, String password){
        return userRepository.login(email,password);
    }

    public MutableLiveData<Boolean> signInWithGoogle(GoogleSignInAccount acct){
        return userRepository.loginWithGoogle(acct);
    }

    public void signOut(){
        userRepository.logout();
    }

    public MutableLiveData<User> currentUser(){
        return userRepository.currentUser();
    }

    public FirebaseUser getCurrentFirebaseUser(){
        return userRepository.getCurrentFirebaseUser();
    }

    public void uploadImageProfileCurrentUser(Uri uri){
         userRepository.uploadeImageProfileCurrentUser(uri);
    }

    public MutableLiveData<Uri> getProfileImageCurrentUser(){
        return userRepository.getProfileImageCurrentUser();
    }

    public MutableLiveData<Uri> getProfileImageOfUser(User user){
        return userRepository.getProfileImageOfUser(user);
    }

    public void updateDataUser(String name, String phone){
        userRepository.updateDataUser(name,phone);
    }

    public void rateUser(String userEmail, Rating rate){
        userRepository.rateUser(userEmail,rate);
    }

    public MutableLiveData<Rating> getRatingOfUser(String userEmail){
        return userRepository.getRatingOfUser(userEmail);
    }

}
