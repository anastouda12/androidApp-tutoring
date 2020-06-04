package com.example.tutoresi.data;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import com.example.tutoresi.model.Rating;
import com.example.tutoresi.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

/**
 * User repository
 */
public class UserRepository {

    private FirebaseSource firebaseSource;
    private static UserRepository instance = null;

    /**
     * Constructor of userRepository
     */
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

    /**
     * Login with email and password
     * @param email email
     * @param password password
     * @return true if the connexion was a success
     */
    public MutableLiveData<Boolean> login(String email, String password){
        return firebaseSource.login(email,password);
    }

    /**
     * Register an user with data information
     * @param email email
     * @param password password
     * @param name name
     * @param phone phone
     * @return code representing the result of the registering
     * ( CODE 0 : SUCCESS, CODE - 1 : WEAK PASSWORD, CODE -2 : INVALID MAIL, CODE -3 : USERALREADY EXiST)
     */
    public MutableLiveData<Integer> register(String email, String password, String name, String phone){
        return firebaseSource.register(email, password,name, phone);
    }

    /**
     * Get the current user connected
     * @return user connected in model structure User(name,email,phone)
     */
    public MutableLiveData<User> currentUser(){
        return firebaseSource.currentUser();
    }

    /**
     * Get currentFirebaseUser
     * @return current firebaseUser connected
     */
    public FirebaseUser getCurrentFirebaseUser(){
        return firebaseSource.getCurrentFirebaseUser();
    }


    /**
     * Logout
     */
    public void logout(){
        firebaseSource.logout();
    }

    /**
     * Store the image of current user in DB FirebaseStorage.
     * @param uri uri of the image.
     * @return true if the image was stores successfully
     */
    public MutableLiveData<Boolean> uploadProfileImageCurrentUser(Uri uri){return firebaseSource.uploadProfileImageCurrentUser(uri);}

    /**
     * Get the profile image of the current user connected
     * @return uri of the image.
     */
    public MutableLiveData<Uri> getProfileImageCurrentUser(){
        return firebaseSource.getAvatarProfileCurrentUser();
    }

    /**
     * Get the profile image of the userEmail given.
     * @param email email of the user
     * @return URI of the image.
     */
    public MutableLiveData<Uri> getProfileImageOfUser(String email){
        return firebaseSource.getAvatarProfileOfUser(email);
    }

    /**
     * Update the data of the current user with data given
     * @param name name
     * @param phone phone
     * @return true if the data was updated successfully
     */
    public MutableLiveData<Boolean> updateDataUser(String name, String phone){
        return firebaseSource.updateDataUser(name,phone);
    }

    /**
     * Rate an user given
     * @param userEmail user to rate
     * @param rate rating
     * @return true if the rating was stored successfully
     */
    public MutableLiveData<Boolean> rateUser(String userEmail, Rating rate){
        return firebaseSource.rateUser(userEmail,rate);
    }

    /**
     * Get rating of a given user
     * @param userEmail user to get the rating
     * @return the rating of the user
     */
    public  MutableLiveData<Rating> getRatingOfUser(String userEmail){
        return firebaseSource.getRatingOfUser(userEmail);
    }

    /**
     * Sign in with google account
     * @param googleSignInAccount google account
     * @return true if the connexion was successfully
     */
    public MutableLiveData<Boolean> loginWithGoogle(GoogleSignInAccount googleSignInAccount){
        return firebaseSource.firebaseAuthWithGoogle(googleSignInAccount);
    }

}
