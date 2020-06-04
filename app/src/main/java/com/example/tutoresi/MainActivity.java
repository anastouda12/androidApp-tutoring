package com.example.tutoresi;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tutoresi.data.UserViewModel;
import com.example.tutoresi.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.squareup.picasso.Picasso;

public class MainActivity extends AbstractActivity {

    public static final int UPDATE_DATA_REQUEST_CODE = 1;

    private Button mBtnFindTutoring, mBtnMyAccount, mBtnReminder, mBtnSignOut;
    private ImageView mAvatarUser;
    private UserViewModel mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private Uri imgUserURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = new ViewModelProvider(this).get(UserViewModel.class);
        mBtnFindTutoring = (Button) findViewById(R.id.btn_findTutoring);
        mBtnMyAccount = (Button) findViewById(R.id.btn_myaccount);
        mBtnReminder = (Button) findViewById(R.id.btn_reminder);
        mBtnSignOut = (Button) findViewById(R.id.btn_signout);
        mAvatarUser = (ImageView) findViewById(R.id.user_avatar);

        mAuth.getProfileImageCurrentUser().observe(this, new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                imgUserURI = uri;
                Picasso.get().invalidate(uri);
                Picasso.get().load(uri).fit().centerCrop().into(mAvatarUser);
            }
        });
        mBtnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                mGoogleSignInClient.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        mBtnReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ReminderActivity.class));
            }
        });

        mBtnMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMyAccount();
            }
        });

        mBtnFindTutoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CourseActivity.class);
                intent.putExtra("EXTRA_CURRENT_USER",mAuth.getCurrentFirebaseUser().getEmail());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UPDATE_DATA_REQUEST_CODE && data != null && resultCode == RESULT_OK){
            boolean dataHasChanged = data.getBooleanExtra("EXTRA_DATA_HAS_CHANGED",false);
            boolean imgHasChanged = data.getBooleanExtra("EXTRA_IMG_HAS_CHANGED",false);
            if (imgHasChanged) {
                Uri uri = Uri.parse(data.getStringExtra("EXTRA_NEW_IMG"));
                imageUploader(uri);
                Picasso.get().invalidate(uri);
                Picasso.get().load(uri).fit().centerCrop().into(mAvatarUser);
            }
            if(dataHasChanged){
                updateDataUser(data.getStringExtra("EXTRA_NEW_NAME"),
                        data.getStringExtra("EXTRA_NEW_PHONE")
                        );
            }
            }
        }


    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Start activity myAccount.
     * Put all data of the user in EXTRA intent.
     */
    private void goToMyAccount(){
        mAuth.currentUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Intent intent = new Intent(MainActivity.this,MyAccountActivity.class);
                intent.putExtra("EXTRA_USER_NAME",user.getName());
                intent.putExtra("EXTRA_USER_EMAIL",user.getEmail());
                intent.putExtra("EXTRA_USER_PHONE",user.getPhone());
                intent.putExtra("EXTRA_USER_IMG",imgUserURI.toString());
               startActivityForResult(intent,UPDATE_DATA_REQUEST_CODE);
            }
        });
    }

    /**
     * Upload the new image profile in DB.
     */
    private void imageUploader(Uri uploadedImg){
        mAuth.uploadProfileImageCurrentUser(uploadedImg).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean) {
                    Toast.makeText(MainActivity.this, R.string.newProfileImgSaved, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this, R.string.uploadAvatarFailed, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Update the data of the user with the new (Phone, name)
     */
    private void updateDataUser(String name, String phone){
        mAuth.updateDataUser(name,phone).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    Toast.makeText(MainActivity.this, R.string.infos_saved, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this, R.string.updateInfosFailed, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
