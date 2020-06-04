package com.example.tutoresi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutoresi.Data.ReminderViewModel;
import com.example.tutoresi.Model.Reminder;
import com.example.tutoresi.Receiver.ReminderBroadcast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReminderActivity extends AbstractActivity {

    public static final int NEW_REMINDER_ACTIVITY_REQUEST_CODE = 1;

    private RecyclerView mRecyclerReminder;
    private FloatingActionButton mBtnAddReminder;
    // FirebaseUI offers RecyclerView adapters for the Realtime Database:
    // FirebaseUI make it easier to bind your data with the UI. updates data in real-time
    // If we don't use firebaseRecycler we need to create our own custom adapter that can work with firebase database.
    private FirebaseRecyclerOptions<Reminder> options; // First, configure the adapter by building FirebaseRecyclerOption
    private FirebaseRecyclerAdapter<Reminder, ReminderViewHolder> adapter;
    private DatabaseReference databaseReference;
    private ReminderViewModel reminderViewModel;



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
        if(requestCode == NEW_REMINDER_ACTIVITY_REQUEST_CODE && data != null && resultCode == RESULT_OK){
            registerReminder(
                    data.getStringExtra("EXTRA_COURSE"),
                    data.getStringExtra("EXTRA_DATETIME"),
                    data.getExtras().getLong("EXTRA_TIME_MILLI"),
                    data.getStringExtra("EXTRA_LOC")
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        mRecyclerReminder = (RecyclerView) findViewById(R.id.recycler_reminder);
        mBtnAddReminder = (FloatingActionButton) findViewById(R.id.btn_addReminder);
        reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);
        createNotificationChannel();

        mRecyclerReminder.setHasFixedSize(true);//  means recycler's size is fixed and is not affected by the adapter contents
        mRecyclerReminder.setLayoutManager(new LinearLayoutManager(this));

        mBtnAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderActivity.this, NewReminderActivity.class);
                startActivityForResult(intent,NEW_REMINDER_ACTIVITY_REQUEST_CODE);
            }
        });

        initRecycler();
        initSwipeItemListener();

    }

    /**
     * Register the reminder in DB
     * @param course course title
     * @param location location title
     */
    private void registerReminder(String course, String dateTime, final long timeInMilli, String location){
        reminderViewModel.addReminder(new Reminder(course,dateTime,location)).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    Toast.makeText(ReminderActivity.this,R.string.reminder_added,Toast.LENGTH_LONG).show();
                    setAlarm(timeInMilli);
                }
            }
        });

    }

    /**
     * Creates notification channel
     */
    private void createNotificationChannel(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "TutorESI Rappel canal";
            String description = getApplicationContext().getString(R.string.channelNotificationReminder);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            // we create the channel here
            // Because we must create the notification channel before posting any notifications on Android 8.0 and higher,
            NotificationChannel channel = new NotificationChannel("notifyTutorEsi",name,importance);
            channel.setDescription(description);
            // Register the channel with the system;
            // Before you can deliver the notification on Android 8.0 and higher,
            // you must register your app's notification channel with the system by passing
            // an instance of NotificationChannel to createNotificationChannel()
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Alarms (based on the AlarmManager class)
     * give a way to perform time-based operations outside the lifetime of the application.
     */
    private void setAlarm(long timeInMillis){
        Intent intent = new Intent(ReminderActivity.this, ReminderBroadcast.class);
        // A pending intent that fires when the alarm is triggered.
        // A Pending Intent specifies an action to take in the future
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ReminderActivity.this,0, intent,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // Time when the alarm is triggered
        // RTC_WAKEUP Wakes up the device to fire the pending intent at the specified time.
        // It will wake up the device out of sleep mode
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,timeInMillis,pendingIntent);
    }

    /**
     * Initialize recycler of reminders
     */
    private void initRecycler(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("reminders");
        databaseReference.keepSynced(true);

        // Set the Firebase query to listen to
        options = new FirebaseRecyclerOptions.Builder<Reminder>().setQuery(databaseReference, Reminder.class).build();

        adapter = new FirebaseRecyclerAdapter<Reminder, ReminderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReminderViewHolder holder, int position, @NonNull Reminder model) {

                holder.setBackgroundColorByPosition(position);
                holder.setMCourse(getString(R.string.Cours)+ " : " + model.getCourse());
                holder.setMLocation(getString(R.string.location)+ " : "+model.getLocation());
                holder.setMDate(getString(R.string.Date)+" : "+model.getDate());

                /*
                   holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });*/
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
                        .setMessage(getApplicationContext().getString(R.string.askDeleteReminder))
                        .setPositiveButton(getApplicationContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ref.removeValue();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(ReminderActivity.this,getApplicationContext().getString(R.string.reminderDeleted),Toast.LENGTH_LONG).show();
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

