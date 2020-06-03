package com.example.tutoresi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class NewTutoringActivity extends AbstractActivity {

    private Button mBtnRegister;
    private EditText mDescriptionTutoring;
    private TextView mTitle;
    private String course_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tutor);

        mBtnRegister = (Button) findViewById(R.id.btn_registerTutor);
        mDescriptionTutoring = (EditText) findViewById(R.id.input_description_tutoring);
        mTitle = (TextView) findViewById(R.id.course_title_tutoring);

        course_id = getIntent().getStringExtra("EXTRA_COURSE_ID");
        mTitle.setText(getResources().getText(R.string.become_tutor)+" "+course_id);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description =  mDescriptionTutoring.getText().toString().trim();
                if (description.length() < 4 || description.length() > 100) {
                    mDescriptionTutoring.setError(getResources().getString(R.string.description_tutoringRequired));
                    return;
                }
                Intent replyIntent = new Intent();
                replyIntent.putExtra("EXTRA_TUTORING_DESC",description);
                setResult(RESULT_OK,replyIntent);
                finish();
            }
        });

    }
}
