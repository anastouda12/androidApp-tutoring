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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutoresi.Model.Reminder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReminderActivity extends AppCompatActivity {

    private RecyclerView mRecyclerReminder;
    private FloatingActionButton mBtnAddReminder;
    private FirebaseRecyclerOptions<Reminder> options;
    private FirebaseRecyclerAdapter<Reminder, ReminderViewHolder> adapter;
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

        mRecyclerReminder = (RecyclerView) findViewById(R.id.recycler_reminder);
        mBtnAddReminder = (FloatingActionButton) findViewById(R.id.btn_addReminder);
        mRecyclerReminder.setHasFixedSize(true);
        mRecyclerReminder.setLayoutManager(new LinearLayoutManager(this));

        mBtnAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderActivity.this, DetailReminderActivity.class);
                startActivity(intent);
            }
        });

        initRecycler();
        initSwipeItemListener();

    }

    /**
     * Initialize recycler of reminders
     */
    private void initRecycler(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("reminders");
        databaseReference.keepSynced(true);

        options = new FirebaseRecyclerOptions.Builder<Reminder>().setQuery(databaseReference, Reminder.class).build();

        adapter = new FirebaseRecyclerAdapter<Reminder, ReminderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReminderViewHolder holder, int position, @NonNull Reminder model) {

                holder.setBackgroundColorByPosition(position);
                holder.setMCourse("Cours : " + model.getCourse());
                holder.setMLocation("Lieu : "+model.getLocation());
                holder.setMDate("Date : "+model.getDate());

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
                return new ReminderViewHolder(LayoutInflater.from(ReminderActivity.this).inflate(R.layout.reminder_view, parent, false));
            }
        };

        mRecyclerReminder.setAdapter(adapter);

    }

    /**
     * Initialize listener of item on swipe
     */
    private void initSwipeItemListener(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                final DatabaseReference ref = adapter.getRef(position);
                new AlertDialog.Builder(ReminderActivity.this)
                        .setMessage("Tu es sûr de vouloir supprimer ce rappel ?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ref.removeValue();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(ReminderActivity.this,"Rappel supprimé",Toast.LENGTH_LONG).show();
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

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerReminder);

    }


    /**
     * Class ViewHolder of recycler reminders
     */
    public class ReminderViewHolder extends RecyclerView.ViewHolder {

        private TextView mCourse, mDate, mLocation;
        private RelativeLayout mLinear;


        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);

            mCourse = (TextView) itemView.findViewById(R.id.course_card_reminder);
            mDate = (TextView) itemView.findViewById(R.id.date_card_reminder);
            mLocation = (TextView) itemView.findViewById(R.id.location_card_reminder);
            mLinear = (RelativeLayout) itemView.findViewById(R.id.background_reminder);

        }

        public void setMCourse(String course) {
            this.mCourse.setText(course);
        }

        public void setMDate(String date) {
            this.mDate.setText(date);
        }

        public void setMLocation(String location) {
            this.mLocation.setText(location);
        }

        /**
         * Set other color background
         */
        public void setBackgroundColorByPosition(int position){
            if(position % 2 == 0){
                mLinear.setBackgroundColor(getResources().getColor(R.color.colorBlue));

            }else{
                mLinear.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }


    }

}

