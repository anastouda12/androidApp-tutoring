package com.example.tutoresi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private EditText mName, mEmail, mPassword, mConfirmPassword, mPhone;
    private Button mBtnRegister;
    private TextView mLoginText;
    private ProgressBar mProgressBar;

    private FirebaseAuth fAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    mDatabase = FirebaseDatabase.getInstance().getReference();

    mName = (EditText) findViewById(R.id.input_name);
    mEmail = (EditText) findViewById(R.id.input_email_register);
    mPassword = (EditText) findViewById(R.id.input_password_register);
    mConfirmPassword = (EditText) findViewById(R.id.input_confirmPassword);
    mPhone = (EditText) findViewById(R.id.input_phone);

    mBtnRegister = (Button) findViewById(R.id.btn_register);
    mLoginText = (TextView) findViewById(R.id.goToLogin_text);

    mProgressBar = (ProgressBar) findViewById(R.id.register_progressBar);

    fAuth = FirebaseAuth.getInstance();

    if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
    }

    mLoginText.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
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


            if(TextUtils.isEmpty(email)){
                mEmail.setError("Email est requis.");
                return;
            }
            if(TextUtils.isEmpty(name)){
                mEmail.setError("Un nom est requis.");
                return;
            }
            if(TextUtils.isEmpty(phone)){
                mEmail.setError("Un numéro de contact est requis.");
                return;
            }
            if(phone.length() != 10){
                mPhone.setError("Le numéro doit être composé de 10 chiffres");
                return;
            }
            if(TextUtils.isEmpty(password)){
                mPassword.setError("Le mots de passe est requis.");
                return;
            }
            if(password.length() < 6){
                mPassword.setError("Le mots de passe doit contenir au moins 6 caractères");
                return;
            }
            if(!confirmPassword.equals(password)){
                mConfirmPassword.setError("Mots de passe différent");
                return;
            }
            if(!email.matches(EMAIL_PATTERN)){
                mEmail.setError("Adresse email non valide");
                return;
            }
            mProgressBar.setVisibility(View.VISIBLE);

            // Registration user here
            fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String email = mEmail.getText().toString().trim();
                        String phone = mPhone.getText().toString().trim();
                        String name = mName.getText().toString().trim();

                        writeNewUser(fAuth.getCurrentUser().getUid(),name,email,phone);
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }else{
                        Toast.makeText(RegisterActivity.this,R.string.register_failed,Toast.LENGTH_LONG).show();
                        mProgressBar.setVisibility(View.GONE);
                    }

                }
            });

        }
    });


    }

    /**
     * Write new user on DB
     * @param userId userid
     * @param name name
     * @param email email
     * @param phone phone
     */
    private void writeNewUser(String userId, String name, String email, String phone) {
        User user = new User(name, email, phone);
        mDatabase.child("users").child(userId).setValue(user);
    }
}
