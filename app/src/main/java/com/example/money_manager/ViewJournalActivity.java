package com.example.money_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ViewJournalActivity extends AppCompatActivity {

    TextView dateLabel, entryView;
    Button backToCalendarButton;
    private databaseControl dbControl;
    private int userId;
    int year, month, day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_journal);
        dateLabel = findViewById(R.id.viewDateLabel);
        entryView = findViewById(R.id.entryText);
        backToCalendarButton = findViewById(R.id.backToCalendarButton);
        dbControl = new databaseControl(this);

        // Retrieve the user_id passed from the previous activity
        Intent intent = getIntent();
        userId = intent.getIntExtra("user_id", -1);
        year = intent.getIntExtra("year", -1);
        month = intent.getIntExtra("month", -1);
        day = intent.getIntExtra("day", -1);


        // Get all journal entries for this user
        ArrayList<String> entries = dbControl.getJournalEntries(userId, year, month, day);

        // displaying the entries
        if (!entries.isEmpty()) {
            StringBuilder allEntries = new StringBuilder();
            for (int i = 0; i < entries.size(); i++) {
                allEntries.append("• ").append(entries.get(i)).append("\n\n");
            }
            entryView.setText(allEntries.toString());
        } else {
            entryView.setText("No entries found");
        }

        if (entries.isEmpty()) {
            Toast.makeText(this, "No journal entries found", Toast.LENGTH_SHORT).show();
        }
        backToCalendarButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(ViewJournalActivity.this, calendarActivity.class);
            backIntent.putExtra("user_id", userId);
            startActivity(backIntent);
            finish();
        });


    }
}
