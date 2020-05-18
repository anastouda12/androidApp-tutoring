package com.example.tutoresi.Data;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.tutoresi.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;


public class AuthViewModel extends ViewModel {

    private UserRepository userRepository;
    private LiveData<User> authenticatedUserLiveData;

    public AuthViewModel(){
        userRepository = new UserRepository();
        authenticatedUserLiveData = new LiveData<User>() {
            @Override
            public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super User> observer) {
                super.observe(owner, observer);
            }
        };
    }

    public void register(String email, String password, String name, String phone){
        authenticatedUserLiveData = userRepository.register(email,password,name,phone);
    }

    public void login(String email, String password){
        authenticatedUserLiveData = userRepository.login(email,password);
    }

    public void signInWithGoogle(GoogleSignInAccount acct){
        authenticatedUserLiveData = userRepository.loginWithGoogle(acct);
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

    public LiveData<User> getAuthenticatedUserLiveData() {
        return authenticatedUserLiveData;
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


}
