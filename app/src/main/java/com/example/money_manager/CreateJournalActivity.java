package com.example.money_manager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateJournalActivity extends AppCompatActivity {

    private databaseControl dbControl;
    private EditText journalContentsEditText;
    private DatePicker datePicker;
    private Button saveButton;
    private int userId;  // Passed from the previous activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journal);

        dbControl = new databaseControl(this);
        journalContentsEditText = findViewById(R.id.journalContentsEditText);
        datePicker = findViewById(R.id.datePicker);
        saveButton = findViewById(R.id.saveButton);

        // Get the user ID passed from the login or calendar activity
        userId = getIntent().getIntExtra("user_id", -1);

        saveButton.setOnClickListener(v -> saveJournalEntry());
    }

    private void saveJournalEntry() {
        String contents = journalContentsEditText.getText().toString().trim();
        if (contents.isEmpty()) {
            Toast.makeText(CreateJournalActivity.this, "Please enter journal contents", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get the selected date from the DatePicker
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1; // DatePicker months are 0-indexed
        int year = datePicker.getYear();

        // Insert the journal entry into the database
        boolean inserted = insertJournalEntry(userId, month, day, year, contents);
        if (inserted) {
            Toast.makeText(CreateJournalActivity.this, "Journal entry saved", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity after saving
        } else {
            Toast.makeText(CreateJournalActivity.this, "Error saving journal entry", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean insertJournalEntry(int userId, int month, int day, int year, String contents) {
        SQLiteDatabase db = dbControl.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(databaseControl.columnJournalMonth, month);
        values.put(databaseControl.columnJournalDay, day);
        values.put(databaseControl.columnJournalYear, year);
        values.put(databaseControl.columnJournalContents, contents);
        values.put(databaseControl.columnUserId, userId);
        // event_id is optional; if not used, it remains null

        long result = db.insert(databaseControl.journalTable, null, values);
        return result != -1;
    }
}
