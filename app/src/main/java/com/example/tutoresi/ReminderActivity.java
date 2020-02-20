package com.example.tutoresi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tutoresi.Model.Reminder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReminderActivity extends AppCompatActivity {

    private RecyclerView mRecyclerReminder;
    private FloatingActionButton mBtnAddReminder;
    private ArrayList<Reminder> reminders;
    private FirebaseRecyclerOptions<Reminder> options;
    private FirebaseRecyclerAdapter<Reminder,ReminderViewHolder> adapter;
    private DatabaseReference databaseReference;


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        reminders = new ArrayList<>();

        mRecyclerReminder = (RecyclerView) findViewById(R.id.recycler_reminder);
        mBtnAddReminder = (FloatingActionButton) findViewById(R.id.btn_addReminder);
        mRecyclerReminder.setHasFixedSize(true);
        mRecyclerReminder.setLayoutManager(new LinearLayoutManager(this));

        mBtnAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderActivity.this,DetailReminderActivity.class);
                startActivity(intent);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("reminders").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.keepSynced(true);

        options = new FirebaseRecyclerOptions.Builder<Reminder>().setQuery(databaseReference,Reminder.class).build();

        adapter = new FirebaseRecyclerAdapter<Reminder, ReminderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReminderViewHolder holder, int position, @NonNull Reminder model) {

                holder.mCourse.setText(model.getCourse());
                holder.mLocation.setText(model.getLocation());
                holder.mDate.setText(model.getDate());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Intent intent = new Intent(ReminderActivity.this,MainActivity.class);
                        // intent.putExtra("course",model.getCourse()); on va donner en extra vers l'autre activité ici
                        // startactivity ici
                    }
                });
            }

            @NonNull
            @Override
            public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ReminderViewHolder(LayoutInflater.from(ReminderActivity.this).inflate(R.layout.reminder_view,parent,false));
            }
        };


        mRecyclerReminder.setAdapter(adapter);

    }

}
