package com.example.money_manager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Map;

public class CreateTransactionActivity extends AppCompatActivity {
    private databaseControl dbControl;
    private Button dateButton, saveButton, backButton;
    private TextInputEditText amountInput, reasonInput;
    private RadioGroup transactionTypeGroup;
    private int userId;
    private int selYear, selMonth, selDay;
    private Spinner goalSpinner;
    ArrayList<String> goalLabels = new ArrayList<>();
    ArrayList<Integer> goalIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);

        dbControl = new databaseControl(this);

        dateButton = findViewById(R.id.dateButton);
        amountInput = findViewById(R.id.amountInput);
        reasonInput = findViewById(R.id.reasonInput);
        transactionTypeGroup = findViewById(R.id.transactionTypeGroup);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);



        goalSpinner = findViewById(R.id.goalSpinner);
        userId = getIntent().getIntExtra("user_id", -1);
        goalLabels.add("No goal");
        goalIds.add(-1);  // Add default 'No goal'
        Map<Integer, String> userGoals = dbControl.getUserGoals(userId);
        for (Map.Entry<Integer, String> entry : userGoals.entrySet()) {
            goalLabels.add(entry.getValue());
            goalIds.add(entry.getKey());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, goalLabels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalSpinner.setAdapter(adapter);

        // Initialize date to today
        Calendar c = Calendar.getInstance();
        selYear = c.get(Calendar.YEAR);
        selMonth = c.get(Calendar.MONTH);
        selDay = c.get(Calendar.DAY_OF_MONTH);
        updateDateText();

        dateButton.setOnClickListener(v -> new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selYear = year;
                    selMonth = month;
                    selDay = dayOfMonth;
                    updateDateText();
                }, selYear, selMonth, selDay
        ).show());

        saveButton.setOnClickListener(v -> saveTransaction());
        backButton.setOnClickListener(v -> finish());
    }

    private void updateDateText() {
        dateButton.setText(String.format("%04d-%02d-%02d", selYear, selMonth + 1, selDay));
    }

    private void saveTransaction() {
        String amtStr = amountInput.getText().toString().trim();
        String reason = reasonInput.getText().toString().trim();
        if (amtStr.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "Enter amount and reason", Toast.LENGTH_SHORT).show();
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amtStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }
        int selectedId = transactionTypeGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Select Income or Expense", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean isIncome = (selectedId == R.id.radioIncome);
        if (!isIncome) amount = -amount;

        // Save to transactions table
        long result = dbControl.insertTransaction(userId, selYear, selMonth + 1, selDay, amount, reason);
        if (result != -1) {
            // Optionally update latest goal savings
            int selectedGoalIndex = goalSpinner.getSelectedItemPosition();
            int selectedGoalId = goalIds.get(selectedGoalIndex);
            if (selectedGoalId != -1) {
                // Only update savings if a valid goal is selected
                updateLatestGoalSavings(amount, selectedGoalId);
            }
            Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving transaction", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLatestGoalSavings(double amount, int selectedGoalId) {
        SQLiteDatabase db = dbControl.getWritableDatabase();
        Cursor c;
        if (selectedGoalId != -1) {
            // Use the selected goal ID from the spinner
            String query = "SELECT " + databaseControl.columnSavings + ", " +
                    databaseControl.columnValue + ", " +
                    databaseControl.columnGoalId +
                    " FROM " + databaseControl.goalTable +
                    " WHERE " + databaseControl.columnUserId + "=? AND " +
                    databaseControl.columnGoalId + "=?";
            c = db.rawQuery(query, new String[]{
                    String.valueOf(userId), String.valueOf(selectedGoalId)
            });
        } else {
            // Fallback to the latest goal by date
            String query = "SELECT " + databaseControl.columnSavings + ", " +
                    databaseControl.columnValue + ", " +
                    databaseControl.columnGoalId +
                    " FROM " + databaseControl.goalTable +
                    " WHERE " + databaseControl.columnUserId + "=? ORDER BY " +
                    databaseControl.columnGoalYear + " DESC, " +
                    databaseControl.columnGoalMonth + " DESC, " +
                    databaseControl.columnGoalDay + " DESC LIMIT 1";
            c = db.rawQuery(query, new String[]{String.valueOf(userId)});
        }

        if (c.moveToFirst()) {
            @SuppressLint("Range") double currentSavings = c.getDouble(c.getColumnIndex(databaseControl.columnSavings));
            @SuppressLint("Range") double target = c.getDouble(c.getColumnIndex(databaseControl.columnValue));
            @SuppressLint("Range") int gid = c.getInt(c.getColumnIndex(databaseControl.columnGoalId));

            double updatedSavings = currentSavings + amount;

            // Update the savings
            android.content.ContentValues vals = new android.content.ContentValues();
            vals.put(databaseControl.columnSavings, updatedSavings);

            // If the updated savings exceed or match the goal target, update the goal status to 1 (complete)
            if (updatedSavings >= target) {
                vals.put(databaseControl.columnGoalStatus, 1);  // Set status to 1 (complete)
            }

            db.update(databaseControl.goalTable, vals,
                    databaseControl.columnGoalId + "=?", new String[]{String.valueOf(gid)});
            dbControl.updateStatus(selectedGoalId);
        }
    }
}