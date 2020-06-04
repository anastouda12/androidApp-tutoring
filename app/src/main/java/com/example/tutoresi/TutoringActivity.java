package com.example.tutoresi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutoresi.data.CourseViewModel;
import com.example.tutoresi.data.UserViewModel;
import com.example.tutoresi.model.Rating;
import com.example.tutoresi.model.Tutoring;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TutoringActivity extends AbstractActivity {

    public static final int NEW_TUTORING_ACTIVITY_REQUEST_CODE = 1;

    private RecyclerView mRecyclerTutoring;
    private FloatingActionButton mBtnRegisterTutor;
    // FirebaseUI offers RecyclerView adapters for the Realtime Database:
    // FirebaseUI make it easier to bind your data with the UI. updates data in real-time
    // If we don't use firebaseRecycler we need to create our own custom adapter that can work with firebase database.
    private FirebaseRecyclerOptions<Tutoring> options; // First, configure the adapter by building FirebaseRecyclerOption
    private FirebaseRecyclerAdapter<Tutoring, TutoringViewHolder> adapter;
    private DatabaseReference databaseReference;
    private String course_id;
    private String currentUser;
    private CourseViewModel courseViewModel;
    private UserViewModel userViewModel;


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
        if(requestCode == NEW_TUTORING_ACTIVITY_REQUEST_CODE && data != null && resultCode == RESULT_OK){
            registerTutoring(data.getStringExtra("EXTRA_TUTORING_DESC"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutoring);

        course_id = getIntent().getStringExtra("EXTRA_COURSE_ID");
        currentUser = getIntent().getStringExtra("EXTRA_CURRENT_USER");
        mBtnRegisterTutor = (FloatingActionButton) findViewById(R.id.btn_registerTutor);

        mBtnRegisterTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutoringActivity.this, NewTutoringActivity.class);
                intent.putExtra("EXTRA_COURSE_ID",course_id);
                startActivityForResult(intent,NEW_TUTORING_ACTIVITY_REQUEST_CODE);
            }
        });
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mRecyclerTutoring = (RecyclerView) findViewById(R.id.recycler_tutor);
        mRecyclerTutoring.setHasFixedSize(true);
        mRecyclerTutoring.setLayoutManager(new LinearLayoutManager(this));
        initRecycler();
        initSwipeItemListener();

    }

    /**
     * Register the current user in the current course
     * @param descriptionTutoring description of the tutoring
     */
    private void registerTutoring(String descriptionTutoring){
        courseViewModel.addTutoring(course_id,descriptionTutoring).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    Toast.makeText(TutoringActivity.this,getApplicationContext().getString(R.string.tutoringCreateSuccessfull)
                            +" dans "+course_id,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Initialize the recycler
     */
    private void initRecycler() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("courses").child(course_id).child("tutoring");
        databaseReference.keepSynced(true);

        // Set the Firebase query to listen to
        options = new FirebaseRecyclerOptions.Builder<Tutoring>().setQuery(databaseReference, Tutoring.class).build();

        adapter = new FirebaseRecyclerAdapter<Tutoring, TutoringViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final TutoringViewHolder holder, int position, @NonNull final Tutoring model) {

                holder.setBackgroundColorByPosition(position);
                holder.setMName(getApplicationContext().getString(R.string.tutor)+" : "+model.getAuthor().getName());
                getRating(model.getAuthor().getEmail(),holder.getMRatingBar());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),ProfileTutorActivity.class);
                        intent.putExtra("EXTRA_TUTOR_NAME",model.getAuthor().getName());
                        intent.putExtra("EXTRA_DESC_TUTORING",model.getDescriptionTutoring());
                        intent.putExtra("EXTRA_TUTOR_EMAIL",model.getAuthor().getEmail());
                        intent.putExtra("EXTRA_TUTOR_PHONE",model.getAuthor().getPhone());
                        intent.putExtra("EXTRA_COURSE_ID",course_id);
                        intent.putExtra("EXTRA_CURRENT_USER",currentUser);
                        intent.putExtra("EXTRA_RATING_USER",holder.getMRatingBar().getRating());
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


    /**
     * Initialize listener of item on swipe
     */
    private void initSwipeItemListener(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                String author = adapter.getItem(position).getAuthor().getEmail();
                if(currentUser.equals(author)){ // checks if currentUser is the author of tutoring
                    final DatabaseReference ref = adapter.getRef(position);
                    new AlertDialog.Builder(TutoringActivity.this)
                            .setMessage(getApplicationContext().getString(R.string.confirmTutoring))
                            .setPositiveButton(getApplicationContext().getText(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(TutoringActivity.this,getApplicationContext().getString(R.string.tutoringDeleted),Toast.LENGTH_LONG).show();
                                            checksEmptyCourse();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(getApplicationContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adapter.notifyItemChanged(position);
                                }
                            })

                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialogInterface) {
                                            adapter.notifyItemChanged(position); }
                                    })
                            .create().show();
                }else {
                    adapter.notifyItemChanged(position);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerTutoring);

    }

    /**
     * Checks if the course has a tutor yet, if not delete the course
     */
    private void checksEmptyCourse(){
        courseViewModel.courseHasTutors(course_id).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(!aBoolean){
                    // delete course no tutoring anymore
                    removeCourse();
                    finish();
                }
            }
        });
    }

    /**
     * Remove the course from the DB.
     */
    private void removeCourse(){
        courseViewModel.removeCourse(course_id).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    Toast.makeText(TutoringActivity.this, getApplicationContext().getString(R.string.tutoringCourse)
                            +" "+course_id
                            +" "+getApplicationContext().getString(R.string.deleted),Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(TutoringActivity.this, getApplicationContext().getString(R.string.deletedFailed)
                            +" "+course_id,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Get rating of the user given and set the value of the ratingBar with it.
     * @param userEmail userEmail
     * @param ratingBar ratingBar
     */
    private void getRating(String userEmail, final RatingBar ratingBar){
        userViewModel.getRatingOfUser(userEmail).observe(this, new Observer<Rating>() {
            @Override
            public void onChanged(Rating rating) {
                ratingBar.setRating(rating.getRate());
            }
        });
    }
    public class TutoringViewHolder extends RecyclerView.ViewHolder {

        private TextView mName;
        private RelativeLayout mRelative;
        private RatingBar mRatingBar;

        public TutoringViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = (TextView) itemView.findViewById(R.id.tutor_name);
            mRelative = (RelativeLayout) itemView.findViewById(R.id.background_tutor);
            mRatingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
            mRatingBar.setFocusable(false);
        }

        public void setMName(String name) {
            this.mName.setText(name);
        }


        public TextView getMName() {
            return mName;
        }

        public RatingBar getMRatingBar() {
            return mRatingBar;
        }

        /**
         * Set other color background
         */
        public void setBackgroundColorByPosition(int position) {
            if (position % 2 == 0) {
                mRelative.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                mRelative.setBackgroundColor(getResources().getColor(R.color.colorBlue));

            }
        }

    }

}
