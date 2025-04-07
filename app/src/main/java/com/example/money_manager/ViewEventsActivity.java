package com.example.money_manager;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ViewEventsActivity extends AppCompatActivity {

    private databaseControl dbControl;
    private int userId;
    private ListView eventsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        dbControl = new databaseControl(this);
        eventsListView = findViewById(R.id.eventsListView);

        // Get the user id passed from the previous activity
        userId = getIntent().getIntExtra("user_id", -1);

        // Query the events for the current user
        ArrayList<String> events = getEventsForUser(userId);

        if (events.isEmpty()) {
            Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show();
        }

        // Use a simple ArrayAdapter to display the events
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, events);
        eventsListView.setAdapter(adapter);
    }

    private ArrayList<String> getEventsForUser(int userId) {
        ArrayList<String> events = new ArrayList<>();
        SQLiteDatabase db = dbControl.getReadableDatabase();

        // Query the event table for month, day, year, and event detail for the current user
        String query = "SELECT " + databaseControl.columnEventMonth + ", " +
                databaseControl.columnEventDay + ", " +
                databaseControl.columnEventYear + ", " +
                databaseControl.columnEventDetail +
                " FROM " + databaseControl.eventTable +
                " WHERE " + databaseControl.columnUserId + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int month = cursor.getInt(cursor.getColumnIndex(databaseControl.columnEventMonth));
                @SuppressLint("Range") int day = cursor.getInt(cursor.getColumnIndex(databaseControl.columnEventDay));
                @SuppressLint("Range") int year = cursor.getInt(cursor.getColumnIndex(databaseControl.columnEventYear));
                @SuppressLint("Range") String detail = cursor.getString(cursor.getColumnIndex(databaseControl.columnEventDetail));

                String event = "Date: " + month + "/" + day + "/" + year + "\n" + detail;
                events.add(event);
            }
            cursor.close();
        }
        return events;
    }
}
