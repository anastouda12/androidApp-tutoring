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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tutoresi.Data.AuthViewModel;
import com.example.tutoresi.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MyAccountActivity extends AppCompatActivity {

    private EditText mName, mEmail, mPhone;
    private ImageView mProfile;
    private Button mBtnSave;
    private AuthViewModel mAuth;
    private DatabaseReference mDatabase;
    private boolean imgHasChanged;
    private Uri uploadedImg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        mName = (EditText) findViewById(R.id.input_name_myaccount);
        mPhone = (EditText) findViewById(R.id.input_phone_myaccount);
        mBtnSave = (Button) findViewById(R.id.btn_save_myaccount);
        mEmail = (EditText) findViewById(R.id.input_email_myaccount);
        mProfile = (ImageView) findViewById(R.id.user_profile);

        mAuth = new ViewModelProvider(this).get(AuthViewModel.class);

        mAuth.currentUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mName.setText(user.getName());
                mPhone.setText(user.getPhone());
                mEmail.setText(user.getEmail());
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());



        mAuth.getProfileImageCurrentUser().observe(this, new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(mProfile);
            }
        });

        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });


        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mName.getText().toString().trim())){
                    mEmail.setError("Nom requis");
                    return;
                }
                if(!TextUtils.isEmpty(mPhone.getText().toString().trim()) && mPhone.getText().toString().trim().length() != 10){
                    mPhone.setError("Le numéro doit être composé de 10 chiffres");
                    return;
                }
                if(imgHasChanged){
                    imageUploader();
                }
                mAuth.updateDataUser(mName.getText().toString().trim(),mPhone.getText().toString().trim());
                finish();
                Toast.makeText(MyAccountActivity.this,R.string.infos_saved,Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            uploadedImg = data.getData();
            mProfile.setImageURI(uploadedImg);
        }
    }

    private void imageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imgHasChanged = true;
        startActivityForResult(intent,1);
    }

    private void imageUploader(){
        mAuth.uploadImageProfileCurrentUser(uploadedImg);
    }


}
