package com.example.tutoresi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutoresi.Model.User;
import com.example.tutoresi.Data.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private EditText mName, mEmail, mPassword, mConfirmPassword, mPhone;
    private Button mBtnRegister;
    private TextView mLoginText;
    private ProgressBar mProgressBar;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mName = (EditText) findViewById(R.id.input_name);
        mEmail = (EditText) findViewById(R.id.input_email_register);
        mPassword = (EditText) findViewById(R.id.input_password_register);
        mConfirmPassword = (EditText) findViewById(R.id.input_confirmPassword);
        mPhone = (EditText) findViewById(R.id.input_phone);
        mBtnRegister = (Button) findViewById(R.id.btn_register);
        mLoginText = (TextView) findViewById(R.id.goToLogin_text);
        mProgressBar = (ProgressBar) findViewById(R.id.register_progressBar);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        mLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();
                String phone = mPhone.getText().toString().trim();
                String name = mName.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email est requis.");
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    mEmail.setError("Un nom est requis.");
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    mEmail.setError("Un numéro de contact est requis.");
                    return;
                }
                if (phone.length() != 10) {
                    mPhone.setError("Le numéro doit être composé de 10 chiffres");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Le mots de passe est requis.");
                    return;
                }
                if (password.length() < 6) {
                    mPassword.setError("Le mots de passe doit contenir au moins 6 caractères");
                    return;
                }
                if (!confirmPassword.equals(password)) {
                    mConfirmPassword.setError("Mots de passe différent");
                    return;
                }
                if (!email.matches(EMAIL_PATTERN)) {
                    mEmail.setError("Adresse email non valide");
                    return;
                }

                registerNewUser(email, password, name, phone);

            }
        });

    }

    private void registerNewUser(String email, String password, String name, String phone) {
        mProgressBar.setVisibility(View.VISIBLE);
        authViewModel.register(email, password, name, phone);
        authViewModel.getAuthenticatedUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this,R.string.register_success,Toast.LENGTH_LONG).show();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
