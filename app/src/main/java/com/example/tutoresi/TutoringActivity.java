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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tutoresi.Model.Tutoring;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TutoringActivity extends AppCompatActivity {

    private RecyclerView mRecyclerTutoring;
    private FirebaseRecyclerOptions<Tutoring> options;
    private FirebaseRecyclerAdapter<Tutoring, TutoringViewHolder> adapter;
    private DatabaseReference databaseReference;
    private String course_id;


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
        setContentView(R.layout.activity_tutoring);

        course_id = (String) getIntent().getStringExtra("course_id");
        mRecyclerTutoring = (RecyclerView) findViewById(R.id.recycler_tutor);
        mRecyclerTutoring.setHasFixedSize(true);
        mRecyclerTutoring.setLayoutManager(new LinearLayoutManager(this));
        initRecycler();

    }

    private void initRecycler() {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("courses").child(course_id).child("tutoring");
        databaseReference.keepSynced(true);

        options = new FirebaseRecyclerOptions.Builder<Tutoring>().setQuery(databaseReference, Tutoring.class).build();

        adapter = new FirebaseRecyclerAdapter<Tutoring, TutoringViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final TutoringViewHolder holder, int position, @NonNull final Tutoring model) {

                holder.setBackgroundColorByPosition(position);
                holder.setMName("Tuteur : "+model.getAuthor().getName());
                holder.setMDescription("Description : "+model.getDescriptionTutoring());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),ProfileTutorActivity.class);
                        intent.putExtra("tutor_name",model.getAuthor().getName());
                        intent.putExtra("description_tutoring",model.getDescriptionTutoring());
                        intent.putExtra("tutor_email",model.getAuthor().getEmail());
                        intent.putExtra("tutor_phone",model.getAuthor().getPhone());
                        intent.putExtra("course_id",course_id);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public TutoringViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new TutoringViewHolder(LayoutInflater.from(TutoringActivity.this).inflate(R.layout.tutor_view, parent, false));
            }
        };

        mRecyclerTutoring.setAdapter(adapter);
    }

    public class TutoringViewHolder extends RecyclerView.ViewHolder {

        private TextView mName, mDescription;
        private RelativeLayout mRelative;

        public TutoringViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = (TextView) itemView.findViewById(R.id.tutor_name);
            mDescription = (TextView) itemView.findViewById(R.id.tutoring_description);
            mRelative = (RelativeLayout) itemView.findViewById(R.id.background_tutor);
        }

        public void setMName(String name) {
            this.mName.setText(name);
        }

        public void setMDescription(String description) {
            this.mDescription.setText(description);
        }

        public TextView getmName() {
            return mName;
        }

        public TextView getmDescription() {
            return mDescription;
        }

        /**
         * Set other color background
         */
        public void setBackgroundColorByPosition(int position) {
            if (position % 2 == 0) {
                mRelative.setBackgroundColor(getResources().getColor(R.color.colorMauve));

            } else if (position % 3 == 0) {
                mRelative.setBackgroundColor(getResources().getColor(R.color.colorBlue));
            } else {
                mRelative.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

            }
        }

    }

}
