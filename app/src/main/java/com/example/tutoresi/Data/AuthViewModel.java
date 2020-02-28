package com.example.tutoresi.Data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.tutoresi.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;


public class AuthViewModel extends ViewModel {

    private UserRepository userRepository;
    private LiveData<User> authenticatedUserLiveData;

    public AuthViewModel(){
        userRepository = new UserRepository();
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

    public FirebaseUser currentUser(){
        return userRepository.currentUser();
    }

    public LiveData<User> getAuthenticatedUserLiveData() {
        return authenticatedUserLiveData;
    }
}
