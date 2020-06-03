package com.example.tutoresi.Data;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.tutoresi.Model.Rating;
import com.example.tutoresi.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;


/**
 * UserViewModel
 */
public class UserViewModel extends ViewModel {

    private UserRepository userRepository;

    /**
     * Constructor of userViewModel
     */
    public UserViewModel(){
        userRepository = UserRepository.getInstance();
    }

    /**
     * Register an user with data given
     * @param email email
     * @param password password
     * @param name name
     * @param phone phone
     * @return code representing the result of the registering
     * ( CODE 0 : SUCCESS, CODE - 1 : WEAK PASSWORD, CODE -2 : INVALID MAIL, CODE -3 : USERALREADY EXiST)
     */
    public LiveData<Integer> register(String email, String password, String name, String phone){
       return userRepository.register(email,password,name,phone);
    }

    /**
     * Login with email and password
     * @param email email
     * @param password password
     * @return true if the connexion was a success
     */
    public LiveData<Boolean> login(String email, String password){
        return userRepository.login(email,password);
    }

    /**
     * Sign in with google account
     * @param acct google account
     * @return true if the connexion was successfully
     */
    public LiveData<Boolean> signInWithGoogle(GoogleSignInAccount acct){
        return userRepository.loginWithGoogle(acct);
    }

    /**
     * Logout
     */
    public void signOut(){
        userRepository.logout();
    }

    /**
     * Get the current user connected
     * @return user connected in model structure User(name,email,phone)
     */
    public LiveData<User> currentUser(){
        return userRepository.currentUser();
    }

    /**
     * Get currentFirebaseUser
     * @return current firebaseUser connected
     */
    public FirebaseUser getCurrentFirebaseUser(){
        return userRepository.getCurrentFirebaseUser();
    }

    /**
     * Store the image of current user in DB FirebaseStorage.
     * @param uri uri of the image.
     * @return true if the image was stores successfully
     */
    public LiveData<Boolean> uploadProfileImageCurrentUser(Uri uri){
         return userRepository.uploadProfileImageCurrentUser(uri);
    }

    /**
     * Get the profile image of the current user connected
     * @return uri of the image.
     */
    public LiveData<Uri> getProfileImageCurrentUser(){
        return userRepository.getProfileImageCurrentUser();
    }

    /**
     * Get the profile image of the userEmail given.
     * @param email email of the user
     * @return URI of the image.
     */
    public LiveData<Uri> getProfileImageOfUser(String email){
        return userRepository.getProfileImageOfUser(email);
    }

    /**
     * Update the data of the current user with data given
     * @param name name
     * @param phone phone
     * @return true if the data was updated successfully
     */
    public LiveData<Boolean> updateDataUser(String name, String phone){
        return userRepository.updateDataUser(name,phone);
    }

    /**
     * Rate an user given
     * @param userEmail user to rate
     * @param rate rating
     * @return true if the rating was stored successfully
     */
    public LiveData<Boolean> rateUser(String userEmail, Rating rate){
        return userRepository.rateUser(userEmail,rate);
    }

    /**
     * Get rating of a given user
     * @param userEmail user to get the rating
     * @return the rating of the user
     */
    public LiveData<Rating> getRatingOfUser(String userEmail){
        return userRepository.getRatingOfUser(userEmail);
    }

}
