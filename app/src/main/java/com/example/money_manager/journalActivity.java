package com.example.money_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class journalActivity extends AppCompatActivity {
    TextView dateLabel;
    EditText journalInput;
    Button saveButton;
    Button backToCalendarButton;
    databaseControl db;
    int userId, year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        dateLabel = findViewById(R.id.dateLabel);
        journalInput = findViewById(R.id.journalInput);
        saveButton = findViewById(R.id.saveButton);
        backToCalendarButton = findViewById(R.id.backToCalendarButton);
        db = new databaseControl(this);

        Intent intent = getIntent();
        userId = intent.getIntExtra("user_id", -1);
        year = intent.getIntExtra("year", -1);
        month = intent.getIntExtra("month", -1);
        day = intent.getIntExtra("day", -1);

        dateLabel.setText("Journal for: " + year + "-" + month + "-" + day);

        // Show all existing entries for the selected date
        StringBuilder allEntries = new StringBuilder();
        for (String entry : db.getJournalEntries(userId, year, month, day)) {
            allEntries.append("• ").append(entry).append("\n\n");
        }
        journalInput.setText(allEntries.toString());

        saveButton.setOnClickListener(v -> {
            String content = journalInput.getText().toString();
            if (!content.isEmpty()) {
             //   db.saveJournalEntry(userId, year, month, day, content);
                Toast.makeText(this, "Journal saved!", Toast.LENGTH_SHORT).show();
                journalInput.setText("");
            } else {
                Toast.makeText(this, "Entry cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });

        backToCalendarButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(journalActivity.this, calendarActivity.class);
            backIntent.putExtra("user_id", userId);
            startActivity(backIntent);
            finish();
        });
    }
}