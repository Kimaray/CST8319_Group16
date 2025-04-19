package com.example.money_manager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.database.Cursor;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateTransactionActivity extends AppCompatActivity {

    private databaseControl dbControl;
    private EditText amountEditText;
    private EditText reasonEditText;
    private DatePicker datePicker;
    private Button saveButton, backToCalendarButton;
    private RadioGroup transactionTypeGroup;
    private int userId;
    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);

        dbControl = new databaseControl(this);

        amountEditText = findViewById(R.id.amountEditText);
        reasonEditText = findViewById(R.id.reasonEditText);
        datePicker = findViewById(R.id.datePicker);

        saveButton = findViewById(R.id.saveButton);
        backToCalendarButton = findViewById(R.id.backToCalendarButton);

        transactionTypeGroup = findViewById(R.id.transactionTypeGroup);

        // Get the user_id passed from the previous activity
        userId = getIntent().getIntExtra("user_id", -1);
        // Get the event_id if applicable; otherwise, set a valid default.
        eventId = getIntent().getIntExtra("event_id", 0);

        saveButton.setOnClickListener(v -> saveTransaction());

        backToCalendarButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(CreateTransactionActivity.this, calendarActivity.class);
            backIntent.putExtra("user_id", userId);
            startActivity(backIntent);
            finish();
        });
    }

    private void saveTransaction() {
        String amountString = amountEditText.getText().toString().trim();
        String reason = reasonEditText.getText().toString().trim();

        if (amountString.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = transactionTypeGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select Income or Expense", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isIncome = (selectedId == R.id.radioIncome);

        // Get the date from the DatePicker (note: month is 0-indexed)
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();

        // Insert the transaction into the database
        boolean inserted = insertTransaction(userId, eventId, amount, reason, month, day, year);
        if (inserted) {
            updateLatestGoalSavings(userId, amount, isIncome);
            Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving transaction", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean insertTransaction(int userId, int eventId, double amount, String reason, int month, int day, int year) {
        SQLiteDatabase db = dbControl.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(databaseControl.columnAmount, amount);
        values.put(databaseControl.columnReason, reason);
        values.put(databaseControl.columnTranMonth, month);
        values.put(databaseControl.columnTranDay, day);
        values.put(databaseControl.columnTranYear, year);
        values.put(databaseControl.columnUserId, userId);
        values.put(databaseControl.columnEventId, eventId);
        return db.insert(databaseControl.transactionTable, null, values) != -1;
    }

    private void updateLatestGoalSavings(int userId, double amount, boolean isIncome) {
        SQLiteDatabase db = dbControl.getWritableDatabase();

        String query = "SELECT " + databaseControl.columnSavings + ", " + databaseControl.columnGoalId +
                " FROM " + databaseControl.goalTable +
                " WHERE " + databaseControl.columnUserId + " = ?" +
                " ORDER BY " + databaseControl.columnGoalYear + " DESC, " +
                databaseControl.columnGoalMonth + " DESC, " +
                databaseControl.columnGoalDay + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        // Updating the total savings for the user
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") double currentSavings = cursor.getDouble(cursor.getColumnIndex(databaseControl.columnSavings));
            @SuppressLint("Range") int goalId = cursor.getInt(cursor.getColumnIndex(databaseControl.columnGoalId));

        // Calculating based on money coming in or out
            double updatedSavings = isIncome ? currentSavings + amount : currentSavings - amount;

            ContentValues values = new ContentValues();
            values.put(databaseControl.columnSavings, updatedSavings);

            db.update(databaseControl.goalTable, values, databaseControl.columnGoalId + " = ?", new String[]{String.valueOf(goalId)});
        }
        cursor.close();
    }
}
