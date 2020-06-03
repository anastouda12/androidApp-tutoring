package com.example.tutoresi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.os.Bundle;

import com.example.tutoresi.Receiver.ConnectionReceiver;

/**
 * AbstractActivity who extends AppCompatActivity
 * It register a connectionReceiver to checks the connection changes
 */
public class AbstractActivity extends AppCompatActivity {

    private ConnectionReceiver receiver; // BroadCastReceiver

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() { // user return to the activity
        super.onResume();
        receiver = new ConnectionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {  // when user go to another activity
        super.onPause();
        this.unregisterReceiver(receiver);
    }
}
