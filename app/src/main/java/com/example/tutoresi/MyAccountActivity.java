package com.example.tutoresi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyAccountActivity extends AppCompatActivity {

    private EditText mName, mEmail, mPhone;
    private Button mBtnSave;
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        mName = (EditText) findViewById(R.id.input_name_myaccount);
        mPhone = (EditText) findViewById(R.id.input_phone_myaccount);
        mBtnSave = (Button) findViewById(R.id.btn_save_myaccount);
        mEmail = (EditText) findViewById(R.id.input_email_myaccount);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mName.setText(dataSnapshot.child("name").getValue().toString());
                mPhone.setText(dataSnapshot.child("phone").getValue().toString());
                mEmail.setText(dataSnapshot.child("email").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mName.getText().toString().trim())){
                    mEmail.setError("Nom requis");
                    return;
                }
                if(TextUtils.isEmpty(mPhone.getText().toString().trim())){
                    mEmail.setError("Numéro de téléphone requis");
                    return;
                }
                if(mPhone.getText().toString().trim().length() != 10){
                    mPhone.setError("Le numéro doit être composé de 10 chiffres");
                    return;
                }
                mDatabase.child("name").setValue(mName.getText().toString().trim());
                mDatabase.child("phone").setValue(mPhone.getText().toString().trim());
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                Toast.makeText(MyAccountActivity.this,R.string.infos_saved,Toast.LENGTH_LONG).show();
            }
        });
    }

}
