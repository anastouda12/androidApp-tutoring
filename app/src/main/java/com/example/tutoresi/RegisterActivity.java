package com.example.tutoresi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutoresi.Data.UserViewModel;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private Uri uploadedImg;

    private EditText mName, mEmail, mPassword, mConfirmPassword, mPhone;
    private CircleImageView mPhoto;
    private Button mBtnRegister;
    private TextView mLoginText;
    private ProgressBar mProgressBar;

    private UserViewModel userViewModel;

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
        mPhoto = (CircleImageView) findViewById(R.id.user_avatar);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
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
                    mEmail.setError(getApplicationContext().getString(R.string.mailRequired));
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    mEmail.setError(getApplicationContext().getString(R.string.nameRequired));
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    mEmail.setError(getApplicationContext().getString(R.string.phoneRequired));
                    return;
                }
                if (phone.length() != 10) {
                    mPhone.setError(getApplicationContext().getString(R.string.phoneNoValid));
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError(getApplicationContext().getString(R.string.passwordRequired));
                    return;
                }
                if (password.length() < 6) {
                    mPassword.setError(getApplicationContext().getString(R.string.passwordPattern));
                    return;
                }
                if (!confirmPassword.equals(password)) {
                    mConfirmPassword.setError(getApplicationContext().getString(R.string.passwordNotSame));
                    return;
                }
                if (!email.matches(EMAIL_PATTERN)) {
                    mEmail.setError(getApplicationContext().getString(R.string.mailNoValid));
                    return;
                }

                registerNewUser(email, password, name, phone);

            }
        });

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

    }

    private void imageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    private void imageUploader(){
        userViewModel.uploadImageProfileCurrentUser(uploadedImg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            uploadedImg = data.getData();
            Picasso.get().load(uploadedImg).fit().centerCrop().into(mPhoto);
        }
    }

    private void registerNewUser(String email, String password, String name, String phone) {
        mProgressBar.setVisibility(View.VISIBLE);
        userViewModel.register(email, password, name, phone).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){ // register ok
                    // If an image is uploaded by the user we store in the db storage.
                    if(mPhoto.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.default_img_user).getConstantState()){
                        imageUploader();
                    }
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this,R.string.register_success,Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                }else{ // register failed
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this,R.string.register_failed,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
