package com.example.tutoresi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tutoresi.Data.CourseViewModel;
import com.example.tutoresi.Model.Course;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class CourseActivity extends AppCompatActivity {

    private RecyclerView mRecyclerCourse;
    private FirebaseRecyclerOptions<Course> options;
    private FirebaseRecyclerAdapter<Course, CourseViewHolder> adapter;
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
        setContentView(R.layout.activity_course);

        mRecyclerCourse = (RecyclerView) findViewById(R.id.recycler_course);
        mRecyclerCourse.setHasFixedSize(true);
        mRecyclerCourse.setLayoutManager(new LinearLayoutManager(this));
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
            protected void onBindViewHolder(@NonNull CourseViewHolder holder, int position, @NonNull Course model) {

                holder.setBackgroundColorByPosition(position);
                holder.setMCourse(model.getId());
                holder.setMDescription(model.getDescription());
                holder.setmLibelle(model.getLibelle());

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



        /**
         * Set other color background
         */
        public void setBackgroundColorByPosition(int position) {
            if (position % 2 == 0) {
                mRelative.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

            } else if (position % 3 == 0) {
                mRelative.setBackgroundColor(getResources().getColor(R.color.colorBlue));
            } else {
                mRelative.setBackgroundColor(getResources().getColor(R.color.colorYellow));

            }
        }

    }

}
