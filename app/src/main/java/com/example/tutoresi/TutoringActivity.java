package com.example.tutoresi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutoresi.Model.Tutoring;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TutoringActivity extends AppCompatActivity {

    private RecyclerView mRecyclerTutoring;
    private FloatingActionButton mBtnRegisterTutor;
    private FirebaseRecyclerOptions<Tutoring> options;
    private FirebaseRecyclerAdapter<Tutoring, TutoringViewHolder> adapter;
    private DatabaseReference databaseReference;
    private String course_id;
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
        setContentView(R.layout.activity_tutoring);

        course_id = (String) getIntent().getStringExtra("course_id");
        mBtnRegisterTutor = (FloatingActionButton) findViewById(R.id.btn_registerTutor);

        mBtnRegisterTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutoringActivity.this, RegisterTutorActivity.class);
                intent.putExtra("course_id",course_id);
                startActivity(intent);
            }
        });
        mRecyclerTutoring = (RecyclerView) findViewById(R.id.recycler_tutor);
        mRecyclerTutoring.setHasFixedSize(true);
        mRecyclerTutoring.setLayoutManager(new LinearLayoutManager(this));
        currentUser = getIntent().getStringExtra("current_user"); // current user
        initRecycler();
        initSwipeItemListener();

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
                if(currentUser.equals(author)){ // checks if currentuser is the author of tutoring
                    final DatabaseReference ref = adapter.getRef(position);
                    new AlertDialog.Builder(TutoringActivity.this)
                            .setMessage("Tu es sûr de ne plus vouloir être tuteur dans ce cours ?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ref.removeValue();
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(TutoringActivity.this,"Tutoring supprimé",Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adapter.notifyItemChanged(position);
                                }
                            })
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialogInterface) {
                                            adapter.notifyItemChanged(position);
                                        }
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
                mRelative.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                mRelative.setBackgroundColor(getResources().getColor(R.color.colorBlue));

            }
        }

    }

}