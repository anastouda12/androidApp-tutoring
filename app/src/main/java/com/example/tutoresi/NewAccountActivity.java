package com.example.tutoresi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.tutoresi.config.ErrorsCode;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutoresi.data.UserViewModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewAccountActivity extends AbstractActivity {

    final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private Uri uploadedImg;

    private EditText mName, mEmail, mPassword, mConfirmPassword, mPhone;
    private CircleImageView mPhoto;
    private Button mBtnRegister;
    private TextView mLoginText;
    private UserViewModel userViewModel;
    private ProgressBar mProgressBar;

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
                    mName.setError(getApplicationContext().getString(R.string.nameRequired));
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    mPhone.setError(getApplicationContext().getString(R.string.phoneRequired));
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

    /**
     * Manages choices of user - Photo with camera or gallery
     */
    private void imageChooser() {
        final CharSequence[] options = {getResources().getString(R.string.takePhoto),
                getResources().getString(R.string.chooseInGalerie),
                getResources().getString(R.string.cancel)};
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.chooseYourPhoto))
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (options[which].equals(getResources().getString(R.string.takePhoto))) {
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);
                        } else if (options[which].equals(getResources().getString(R.string.chooseInGalerie))) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, 1);
                        } else if (options[which].equals(getResources().getString(R.string.cancel))) {
                            dialog.dismiss();
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_menu_camera)
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0://actionCode camera
                if (resultCode == RESULT_OK) {
                    Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                    mPhoto.setImageBitmap(selectedImage);
                    String imgUrl = MediaStore.Images.Media.insertImage(getContentResolver(), selectedImage, "", "");
                    uploadedImg = Uri.parse(imgUrl);
                    return;
                }
            case 1://actionCode gallery
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    uploadedImg = data.getData();
                    mPhoto.setImageURI(uploadedImg);
                    return;
                }

        }

    }

    /**
     * Register a new user in DB.
     * @param email email
     * @param password password
     * @param name name
     * @param phone phone
     */
    private void registerNewUser(String email, String password, String name, String phone) {
        mProgressBar.setVisibility(View.VISIBLE);
        userViewModel.register(email, password, name, phone).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer code) {
                mProgressBar.setVisibility(View.GONE);
                switch (code) {
                    case ErrorsCode.SUCCESS_REGISTER:
                        // If an image is uploaded by the user we store in the db storage.
                        if (mPhoto.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.default_img_user).getConstantState()) {
                            imageUploader();
                        }
                        Toast.makeText(NewAccountActivity.this, R.string.register_success, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(NewAccountActivity.this, MainActivity.class));
                        finish();
                        break;
                    case ErrorsCode.ERROR_INVALID_EMAIL:
                        Toast.makeText(NewAccountActivity.this, R.string.register_failed_emailInvalid, Toast.LENGTH_LONG).show();
                        break;
                    case ErrorsCode.ERROR_WEAK_PASSWORD:
                        Toast.makeText(NewAccountActivity.this, R.string.register_failed_weakPassword, Toast.LENGTH_LONG).show();
                        break;
                    case ErrorsCode.ERROR_USER_EXISTS:
                        Toast.makeText(NewAccountActivity.this, R.string.register_failed_userAlreadyExists, Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(NewAccountActivity.this, R.string.register_failed, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Upload image of the user in storage
     */
    private void imageUploader() {
        userViewModel.uploadProfileImageCurrentUser(uploadedImg);
    }

}
