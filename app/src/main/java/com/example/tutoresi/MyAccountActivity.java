package com.example.tutoresi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MyAccountActivity extends AbstractActivity {

    private EditText mName, mEmail, mPhone;
    private ImageView mProfile;
    private Button mBtnSave;
    private boolean imgHasChanged = false;
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

        mName.setText(getIntent().getStringExtra("EXTRA_USER_NAME"));
        mPhone.setText(getIntent().getStringExtra("EXTRA_USER_PHONE"));
        mEmail.setText(getIntent().getStringExtra("EXTRA_USER_EMAIL"));
        System.out.println("kevin"+Uri.parse(getIntent().getStringExtra("EXTRA_USER_IMG")));
        Picasso.get().load(Uri.parse(getIntent().getStringExtra("EXTRA_USER_IMG"))).memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE).fit().centerCrop().into(mProfile);

        // to checks after if the data was changed;
        final String saveName = mName.getText().toString().trim();
        final String savePhone = mName.getText().toString().trim();

        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mName.getText().toString().trim();
                String phone = mPhone.getText().toString().trim();
                if(TextUtils.isEmpty(name)){
                    mEmail.setError(getResources().getString(R.string.nameRequired));
                    return;
                }
                if(TextUtils.isEmpty(phone) || phone.length() != 10){
                    mPhone.setError(getResources().getString(R.string.phonoDigitError));
                    return;
                }
                boolean dataHasChanged = !saveName.equals(name) && !savePhone.equals(phone);
                Intent replyIntent = new Intent();
                replyIntent.putExtra("EXTRA_DATA_HAS_CHANGED",dataHasChanged);
                replyIntent.putExtra("EXTRA_IMG_HAS_CHANGED",imgHasChanged);
                if(dataHasChanged){
                    replyIntent.putExtra("EXTRA_NEW_PHONE",phone);
                    replyIntent.putExtra("EXTRA_NEW_NAME",name);
                }
                if(imgHasChanged) {
                    replyIntent.putExtra("EXTRA_NEW_IMG", uploadedImg.toString());
                }
                setResult(RESULT_OK,replyIntent);
                finish();
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
                    String imgUrl = MediaStore.Images.Media.insertImage(getContentResolver(), selectedImage, "", "");
                    uploadedImg = Uri.parse(imgUrl);
                    imgHasChanged = true;
                    return;
                }
            case 1://actionCode gallery
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    uploadedImg = data.getData();
                    mProfile.setImageURI(uploadedImg);
                    imgHasChanged = true;
                    return;
                }

        }

        }

    /**
     * Image chooser (CAMERA - GALLERY)
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


}
