package com.example.tutoresi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutoresi.Data.UserViewModel;
import com.example.tutoresi.Model.Rating;
import com.example.tutoresi.Model.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileTutorActivity extends AppCompatActivity {

    private TextView mTutorName, mDescriptionTutoring, mCourse;
    private Button mContactMail, mContactPhone;
    private UserViewModel mAuth;
    private String mTutorEmail, mTutorPhone;
    private CircleImageView mTutorAvatar;
    private RatingBar mRatingBar;
    private String courseTutoring;
    private int nbAttempt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_tutor);

        mTutorName = (TextView) findViewById(R.id.tutor_name);
        mDescriptionTutoring = (TextView) findViewById(R.id.tutoring_description);
        mContactMail = (Button) findViewById(R.id.btn_mail_tutor);
        mContactPhone = (Button) findViewById(R.id.btn_call_tutor);
        mTutorAvatar = (CircleImageView) findViewById(R.id.tutor_profile);
        mCourse = (TextView) findViewById(R.id.course_title);
        mRatingBar = (RatingBar) findViewById(R.id.rating_bar);

        mAuth = new ViewModelProvider(this).get(UserViewModel.class);

        mTutorEmail = getIntent().getStringExtra("tutor_email");
        mTutorPhone = getIntent().getStringExtra("tutor_phone");
        courseTutoring = getIntent().getStringExtra("course_id");

        mCourse.setText(courseTutoring);
        mTutorName.setText(getIntent().getStringExtra("tutor_name"));
        mDescriptionTutoring.setText(getIntent().getStringExtra("description_tutoring"));

        mAuth.getProfileImageOfUser(new User(mTutorName.getText().toString(), mTutorEmail, mTutorPhone)).observe(this, new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(mTutorAvatar);
            }
        });


        mContactPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mTutorPhone, null)));
            }
        });

        mContactMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mailto = "mailto:" +
                        mTutorEmail +
                        "?cc=" +
                        "&subject=" + Uri.encode("Tutorat " + courseTutoring + " - TutorESI") +
                        "&body=" + Uri.encode("Bonjour, je suis interessé par le tutorat de " + courseTutoring + " que vous proposez sur l'Application TutorESI");
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse(mailto));
                try {
                    startActivity(emailIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Error to open email app", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAuth.getRatingOfUser(mTutorEmail).observe(this, new Observer<Rating>() {
            @Override
            public void onChanged(Rating rating) {
                mRatingBar.setRating(rating.getRate());
            }
        });

        nbAttempt = 0;
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    if(mAuth.getCurrentFirebaseUser().getEmail().equals(mTutorEmail)){
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.ratingOneself), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (nbAttempt < 4) {
                        mAuth.rateUser(mTutorEmail, new Rating(rating));
                        ++nbAttempt;
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.spam), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
