package com.example.tutoresi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button mBtnCourse, mBtnMyAccount, mBtnReminder, mBtnSignOut;
    private ImageView mAvatarUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }

        mBtnCourse = (Button) findViewById(R.id.btn_course);
        mBtnMyAccount = (Button) findViewById(R.id.btn_myaccount);
        mBtnReminder = (Button) findViewById(R.id.btn_reminder);
        mBtnSignOut = (Button) findViewById(R.id.btn_signout);

        mAvatarUser = (ImageView) findViewById(R.id.user_avatar);

        mBtnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

        mBtnReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ReminderActivity.class));
            }
        });

        mBtnMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MyAccountActivity.class));
            }
        });

    }
}
