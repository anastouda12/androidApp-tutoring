package com.example.tutoresi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.tutoresi.Data.UserViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private EditText mUserEmail, mUserPassword;
    private TextView mCreateAccount;
    private Button mBtnLogin;
    private ProgressBar mProgressBar;
    private static final String  TAG = "LOGIN_ACTIVITY";
    private UserViewModel userViewModel;

    // Configure Google Sign In
    GoogleSignInOptions gso;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserEmail = (EditText) findViewById(R.id.input_email_login);
        mUserPassword = (EditText) findViewById(R.id.input_password_login);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mProgressBar = (ProgressBar) findViewById(R.id.login_progressBar);
        mCreateAccount = (TextView) findViewById(R.id.create_account);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        signInButton = findViewById(R.id.sign_in_button);

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                    // ...
                }
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mUserEmail.getText().toString().trim();
                String password = mUserPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mUserEmail.setError(getApplicationContext().getString(R.string.mailRequired));
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mUserPassword.setError(getApplicationContext().getString(R.string.passwordRequired));
                    return;
                }

                regularLogIn(email, password);
            }
        });
        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });

    }

    private void regularLogIn(String email, String password){
        mProgressBar.setVisibility(View.VISIBLE);
        Toast.makeText(LoginActivity.this,R.string.connexion_loading,Toast.LENGTH_LONG).show();
        userViewModel.login(email,password).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    Toast.makeText(LoginActivity.this,R.string.login_success,Toast.LENGTH_LONG).show();
                    mProgressBar.setVisibility(View.GONE);
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }else{
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this,R.string.login_failed,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signInWithGoogle(final GoogleSignInAccount acct){
        mProgressBar.setVisibility(View.VISIBLE);
        userViewModel.signInWithGoogle(acct).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){ // Sign in ok
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this,R.string.login_success,Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }else { // Sign in ko
                    Toast.makeText(LoginActivity.this,R.string.login_failed,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                signInWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }


}
