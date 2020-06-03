package com.example.tutoresi;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.tutoresi.Model.Course;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CourseActivity extends AbstractActivity {

    private RecyclerView mRecyclerCourse;
    private FloatingActionButton mbtnAddCourse;
    // FirebaseUI offers RecyclerView adapters for the Realtime Database:
    // FirebaseUI make it easier to bind your data with the UI. updates data in real-time
    // If we don't use firebaseRecycler we need to create our own custom adapter that can work with firebase database.
    private FirebaseRecyclerOptions<Course> options; // First, configure the adapter by building FirebaseRecyclerOption
    private FirebaseRecyclerAdapter<Course, CourseViewHolder> adapter;
    private DatabaseReference databaseReference;
    private String currentUser;

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
        setContentView(R.layout.activity_course);

        mbtnAddCourse = (FloatingActionButton) findViewById(R.id.btn_addCourse);
        mbtnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseActivity.this, BecomeTutorActivity.class);
                startActivity(intent);
            }
        });
        mRecyclerCourse = (RecyclerView) findViewById(R.id.recycler_course);
        mRecyclerCourse.setHasFixedSize(true);
        mRecyclerCourse.setLayoutManager(new LinearLayoutManager(this));
        currentUser = getIntent().getStringExtra("current_user"); // current user
        initRecycler();

    }


    /**
     * Initialize recycler of reminders
     */
    private void initRecycler() {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("courses");
        databaseReference.keepSynced(true);

        options = new FirebaseRecyclerOptions.Builder<Course>().setQuery(databaseReference, Course.class).build();

        adapter = new FirebaseRecyclerAdapter<Course, CourseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CourseViewHolder holder, int position, @NonNull Course model) {

                holder.setBackgroundColorByPosition(position);
                holder.setMCourse(model.getId());
                holder.setMDescription(model.getDescription());
                holder.setmLibelle(model.getLibelle());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),TutoringActivity.class);
                        intent.putExtra("course_id",holder.getmCourse().getText().toString());
                        intent.putExtra("current_user",currentUser);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new CourseViewHolder(LayoutInflater.from(CourseActivity.this).inflate(R.layout.course_view, parent, false));
            }
        };

        mRecyclerCourse.setAdapter(adapter);

    }

    /**
     * Class ViewHolder of recycler reminders
     */
    public class CourseViewHolder extends RecyclerView.ViewHolder {

        private TextView mCourse, mDescription, mLibelle;
        private RelativeLayout mRelative;


        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);

            mCourse = (TextView) itemView.findViewById(R.id.course_title);
            mDescription = (TextView) itemView.findViewById(R.id.course_description);
            mLibelle = (TextView) itemView.findViewById(R.id.course_libelle);
            mRelative = (RelativeLayout) itemView.findViewById(R.id.background_course);

        }

        public void setMCourse(String course) {
            this.mCourse.setText(course);
        }

        public void setMDescription(String description) {
            this.mDescription.setText(description);
        }

        public void setmLibelle(String libelle) {
            this.mLibelle.setText(libelle);
        }

        public TextView getmCourse() {
            return mCourse;
        }

        public TextView getmDescription() {
            return mDescription;
        }

        public TextView getmLibelle() {
            return mLibelle;
        }

        /**
         * Set other color background
         */
        public void setBackgroundColorByPosition(int position) {
            if (position % 2 == 0) {
                mRelative.setBackgroundColor(getResources().getColor(R.color.colorBeige));

            } else{
                mRelative.setBackgroundColor(getResources().getColor(R.color.colorTaupe));
            }
        }

    }

}
