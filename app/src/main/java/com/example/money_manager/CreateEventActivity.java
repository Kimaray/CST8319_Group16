package com.example.money_manager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateEventActivity extends AppCompatActivity {

    private databaseControl dbControl;
    private DatePicker datePicker;
    private EditText eventDetailEditText;
    private EditText transactionIdEditText;
    private Button saveButton, cancelButton; // Added cancelButton
    private int userId; // Passed from the previous activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        dbControl = new databaseControl(this);
        datePicker = findViewById(R.id.datePicker);
        eventDetailEditText = findViewById(R.id.eventDetailEditText);
        transactionIdEditText = findViewById(R.id.transactionIdEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);  // Initialize the cancel button

        // Get the user ID from the previous activity
        userId = getIntent().getIntExtra("user_id", -1);

        saveButton.setOnClickListener(v -> saveEvent());
        // Cancel button listener simply finishes the activity
        cancelButton.setOnClickListener(v -> finish());
    }

    private void saveEvent() {
        String eventDetail = eventDetailEditText.getText().toString().trim();
        String transactionIdStr = transactionIdEditText.getText().toString().trim();

        if (eventDetail.isEmpty() || transactionIdStr.isEmpty()) {
            Toast.makeText(CreateEventActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int transactionId;
        try {
            transactionId = Integer.parseInt(transactionIdStr);
        } catch (NumberFormatException e) {
            Toast.makeText(CreateEventActivity.this, "Invalid Transaction ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the selected date (DatePicker months are 0-indexed)
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();

        boolean inserted = insertEvent(userId, transactionId, month, day, year, eventDetail);
        if (inserted) {
            Toast.makeText(CreateEventActivity.this, "Event created", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(CreateEventActivity.this, "Error creating event", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean insertEvent(int userId, int transactionId, int month, int day, int year, String detail) {
        SQLiteDatabase db = dbControl.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(databaseControl.columnEventMonth, month);
        values.put(databaseControl.columnEventDay, day);
        values.put(databaseControl.columnEventYear, year);
        values.put(databaseControl.columnEventDetail, detail);
        values.put(databaseControl.columnUserId, userId);
        values.put(databaseControl.columnTranId, transactionId);
        // The journal_id is optional; it's not included here

        long result = db.insert(databaseControl.eventTable, null, values);
        return result != -1;
    }
}
