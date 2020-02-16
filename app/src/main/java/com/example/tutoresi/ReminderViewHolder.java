package com.example.tutoresi;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReminderViewHolder extends RecyclerView.ViewHolder {

    public TextView mCourse, mDate, mLocation;


    public ReminderViewHolder(@NonNull View itemView){
        super(itemView);

        mCourse = (TextView) itemView.findViewById(R.id.course_card_reminder);
        mDate = (TextView) itemView.findViewById(R.id.date_card_reminder);
        mLocation = (TextView) itemView.findViewById(R.id.location_card_reminder);

    }



}
