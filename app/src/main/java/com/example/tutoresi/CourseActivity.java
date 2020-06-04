package com.example.tutoresi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutoresi.config.ErrorsCode;
import com.example.tutoresi.data.CourseViewModel;
import com.example.tutoresi.model.Course;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CourseActivity extends AbstractActivity {

    public static final int NEW_COURSE_ACTIVITY_REQUEST_CODE = 1;

    private RecyclerView mRecyclerCourse;
    private FloatingActionButton mBtnAddCourse;
    // FirebaseUI offers RecyclerView adapters for the Realtime Database:
    // FirebaseUI make it easier to bind your data with the UI. updates data in real-time
    // If we don't use firebaseRecycler we need to create our own custom adapter that can work with firebase database.
    private FirebaseRecyclerOptions<Course> options; // First, configure the adapter by building FirebaseRecyclerOption
    private FirebaseRecyclerAdapter<Course, CourseViewHolder> adapter;
    private DatabaseReference databaseReference;
    private CourseViewModel courseViewModel;
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == NEW_COURSE_ACTIVITY_REQUEST_CODE && data != null && resultCode == RESULT_OK){
            addCourseTutoring(
                    data.getStringExtra("EXTRA_COURSE_TITLE"),
                    data.getStringExtra("EXTRA_COURSE_LIBEL"),
                    data.getStringExtra("EXTRA_COURSE_DESC"),
                    data.getStringExtra("EXTRA_TUTORING_DESC")
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        mBtnAddCourse = (FloatingActionButton) findViewById(R.id.btn_addCourse);
        mBtnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseActivity.this, NewCourseActivity.class);
                startActivityForResult(intent,NEW_COURSE_ACTIVITY_REQUEST_CODE);
            }
        });
        mRecyclerCourse = (RecyclerView) findViewById(R.id.recycler_course);
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        mRecyclerCourse.setHasFixedSize(true);
        mRecyclerCourse.setLayoutManager(new LinearLayoutManager(this));
        currentUser = getIntent().getStringExtra("EXTRA_CURRENT_USER"); // current user
        initRecycler();

    }

    /**
     * Create new course of tutoring and register the current user of tutor inside that course
     * @param courseTitle course title
     * @param libelle libelle course
     * @param descCourse description of the course
     * @param descTutoring description of the tutoring
     */
    private void addCourseTutoring(String courseTitle,String libelle, String descCourse, String descTutoring){
        final Course course = new Course(courseTitle,libelle, descCourse);
        courseViewModel.addCourse(course).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == ErrorsCode.COURSE_ADD_SUCCESS){
                    Toast.makeText(CourseActivity.this,getApplicationContext().getString(R.string.courseAddedSucces)
                            ,Toast.LENGTH_SHORT).show();
                }else if(integer == ErrorsCode.COURSE_ALREADY_EXIST){
                    Toast.makeText(CourseActivity.this,getApplicationContext().getString(R.string.courseAlreadyExist)
                            ,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CourseActivity.this,getApplicationContext().getString(R.string.courseAddedFailed)
                            ,Toast.LENGTH_SHORT).show();
                }
            }
        });
        courseViewModel.addTutoring(course.getId(),descTutoring).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    Toast.makeText(CourseActivity.this,getApplicationContext().getString(R.string.tutoringCreateSuccessfull)
                            +" dans "+course.getId(),Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(CourseActivity.this,getApplicationContext().getString(R.string.tutoringCreateFailed)
                            +" dans "+course.getId(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * Initialize recycler of reminders
     */
    private void initRecycler() {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("courses");
        databaseReference.keepSynced(true);

        // Set the Firebase query to listen to
        options = new FirebaseRecyclerOptions.Builder<Course>().setQuery(databaseReference, Course.class).build();

        adapter = new FirebaseRecyclerAdapter<Course, CourseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CourseViewHolder holder, int position, @NonNull Course model) {

                holder.setBackgroundColorByPosition(position);
                holder.setMCourse(model.getId());
                holder.setMDescription(model.getDescription());
                holder.setMLib(model.getLibelle());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CourseActivity.this,TutoringActivity.class);
                        intent.putExtra("EXTRA_COURSE_ID",holder.getMCourse().getText().toString());
                        intent.putExtra("EXTRA_CURRENT_USER",currentUser);
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

        private TextView mCourse, mDescription, mLib;
        private RelativeLayout mRelative;


        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);

            mCourse = (TextView) itemView.findViewById(R.id.course_title);
            mDescription = (TextView) itemView.findViewById(R.id.course_description);
            mLib = (TextView) itemView.findViewById(R.id.course_libelle);
            mRelative = (RelativeLayout) itemView.findViewById(R.id.background_course);

        }

        public void setMCourse(String course) {
            this.mCourse.setText(course);
        }

        public void setMDescription(String description) {
            this.mDescription.setText(description);
        }

        public void setMLib(String lib) {
            this.mLib.setText(lib);
        }

        public TextView getMCourse() {
            return mCourse;
        }

        public TextView getMDescription() {
            return mDescription;
        }

        public TextView getMLib() {
            return mLib;
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
