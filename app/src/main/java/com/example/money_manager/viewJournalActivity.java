package com.example.money_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class viewJournalActivity extends AppCompatActivity {

    TextView dateLabel, entryView;
    Button backToCalendarButton;
    databaseControl db;
    int userId, year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_journal);

        dateLabel = findViewById(R.id.viewDateLabel);
        entryView = findViewById(R.id.entryText);
        backToCalendarButton = findViewById(R.id.backToCalendarButton);
        db = new databaseControl(this);

        // getting the date for the journal from the calendar
        Intent intent = getIntent();
        userId = intent.getIntExtra("user_id", -1);
        year = intent.getIntExtra("year", -1);
        month = intent.getIntExtra("month", -1);
        day = intent.getIntExtra("day", -1);

        dateLabel.setText("Journal: " + year + "-" + month + "-" + day);

        // Entries for the specific date to view
        ArrayList<String> entries = db.getJournalEntries(userId, year, month, day);

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

        backToCalendarButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(viewJournalActivity.this, calendarActivity.class);
            backIntent.putExtra("user_id", userId);
            startActivity(backIntent);
            finish();
        });
    }
}