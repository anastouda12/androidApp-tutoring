package com.example.tutoresi.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.tutoresi.Model.User;


public class AuthViewModel extends ViewModel {

    private UserRepository userRepository;
    private LiveData<User> authenticatedUserLiveData;

    public AuthViewModel(){
        userRepository = new UserRepository();
    }

    public void register(String email, String password, String name, String phone){
        authenticatedUserLiveData = userRepository.register(email,password,name,phone);
    }

    public LiveData<User> getAuthenticatedUserLiveData() {
        return authenticatedUserLiveData;
    }
}
