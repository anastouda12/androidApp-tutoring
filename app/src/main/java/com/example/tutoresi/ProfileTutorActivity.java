package com.example.tutoresi;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutoresi.Data.UserViewModel;
import com.example.tutoresi.Model.Rating;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileTutorActivity extends AbstractActivity {

    private final String KEY_NB_ATTEMPT = ""; // when change orientation to save the number of attempt ratings

    private TextView mTutorName, mDescriptionTutoring, mCourse;
    private Button mContactMail, mContactPhone;
    private UserViewModel mAuth;
    private CircleImageView mTutorAvatar;
    private RatingBar mRatingBar;

    private String currentUser, courseTutoring, mTutorEmail, mTutorPhone;
    private int nbAttempt;
    private boolean isUserProfile; // true if its current user connected profile

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_NB_ATTEMPT,nbAttempt);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_tutor);
        if(savedInstanceState != null) { //null if we just start the activity first time
            nbAttempt = savedInstanceState.getInt(KEY_NB_ATTEMPT);
            System.out.println(nbAttempt);
        }else{
            nbAttempt = 0;
        }
        mTutorName = (TextView) findViewById(R.id.tutor_name);
        mDescriptionTutoring = (TextView) findViewById(R.id.tutoring_description);
        mContactMail = (Button) findViewById(R.id.btn_mail_tutor);
        mContactPhone = (Button) findViewById(R.id.btn_call_tutor);
        mTutorAvatar = (CircleImageView) findViewById(R.id.tutor_profile);
        mCourse = (TextView) findViewById(R.id.course_title);
        mRatingBar = (RatingBar) findViewById(R.id.rating_bar);

        mAuth = new ViewModelProvider(this).get(UserViewModel.class);

        currentUser = getIntent().getStringExtra("EXTRA_CURRENT_USER");
        mTutorEmail = getIntent().getStringExtra("EXTRA_TUTOR_EMAIL");
        mTutorPhone = getIntent().getStringExtra("EXTRA_TUTOR_PHONE");
        courseTutoring = getIntent().getStringExtra("EXTRA_COURSE_ID");

        mTutorName.setText(getIntent().getStringExtra("EXTRA_TUTOR_NAME"));
        mRatingBar.setRating(getIntent().getFloatExtra("EXTRA_RATING_USER",0));
        isUserProfile = currentUser.equals(mTutorEmail);
        mCourse.setText(courseTutoring);
        mDescriptionTutoring.setText(getIntent().getStringExtra("EXTRA_DESC_TUTORING"));

        mAuth.getProfileImageOfUser(mTutorEmail).observe(this, new Observer<Uri>() {
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
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.errorOpeningMail), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    if(isUserProfile){
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.ratingOneself), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (nbAttempt < 2) {
                        rateUser(rating);
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.spam), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * Rate the user profile
     * @param rating the rating.
     */
    private void rateUser(Float rating){
        mAuth.rateUser(mTutorEmail, new Rating(rating)).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    nbAttempt++;
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.ratingDone), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.ratingFailed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
