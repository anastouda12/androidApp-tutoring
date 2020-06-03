package com.example.tutoresi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tutoresi.Data.UserViewModel;
import com.example.tutoresi.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MyAccountActivity extends AbstractActivity {

    private EditText mName, mEmail, mPhone;
    private ImageView mProfile;
    private Button mBtnSave;
    private UserViewModel mAuth;
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

        mAuth = new ViewModelProvider(this).get(UserViewModel.class);

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
                    mEmail.setError(getResources().getString(R.string.nameRequired));
                    return;
                }
                if(!TextUtils.isEmpty(mPhone.getText().toString().trim()) && mPhone.getText().toString().trim().length() != 10){
                    mPhone.setError(getResources().getString(R.string.phonoDigitError));
                    return;
                }
                updateDataUser();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0://actionCode camera
                if (resultCode == RESULT_OK) {
                    Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                    mProfile.setImageBitmap(selectedImage);
                    String imgeUrl = MediaStore.Images.Media.insertImage(getContentResolver(), selectedImage, "", "");
                    uploadedImg = Uri.parse(imgeUrl);
                    return;
                }
            case 1://actionCode gallery
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    uploadedImg = data.getData();
                    mProfile.setImageURI(uploadedImg);
                    return;
                }

        }

        }


    /**
     * Image chooser (CAMERA - GALERY)
     */
    private void imageChooser(){
        final CharSequence[] options = { getResources().getString(R.string.takePhoto),
                getResources().getString(R.string.chooseInGalerie),
                getResources().getString(R.string.cancel) };
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.chooseYourPhoto))
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       if(options[which].equals(getResources().getString(R.string.takePhoto))){
                           Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                           imgHasChanged = true;
                           startActivityForResult(takePicture, 0);
                       }else if(options[which].equals(getResources().getString(R.string.chooseInGalerie))){
                           Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                   android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                           imgHasChanged = true;
                           startActivityForResult(pickPhoto , 1);
                       }else if(options[which].equals(getResources().getString(R.string.cancel))){
                           dialog.dismiss();
                       }
                    }
                })
                .setIcon(android.R.drawable.ic_menu_camera)
                .show();
    }

    /**
     * Upload the new image profile in DB.
     */
    private void imageUploader(){
        mAuth.uploadProfileImageCurrentUser(uploadedImg).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean) {
                    Toast.makeText(MyAccountActivity.this, R.string.infos_saved, Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(MyAccountActivity.this, R.string.uploadAvatarFailed, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Update the data of the user with the new (Phone, name, image profile)
     */
    private void updateDataUser(){
        mAuth.updateDataUser(mName.getText().toString().trim(),mPhone.getText().toString().trim()).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    if(imgHasChanged) {
                        imageUploader();
                    }
                }else{
                    Toast.makeText(MyAccountActivity.this, R.string.updateInfosFailed, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
