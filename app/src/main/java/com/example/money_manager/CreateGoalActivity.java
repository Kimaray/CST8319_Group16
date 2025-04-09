package com.example.money_manager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.money_manager.NotificationHelper;

public class  CreateGoalActivity extends AppCompatActivity {

    private databaseControl dbControl;
    private DatePicker datePicker;
    private EditText goalTypeEditText;
    private EditText valueEditText;
    private EditText savingsEditText;
    private EditText descriptionEditText;
    private Button saveButton, cancelButton;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_goal);

        dbControl = new databaseControl(this);
        datePicker = findViewById(R.id.datePicker);
        goalTypeEditText = findViewById(R.id.goalTypeEditText);
        valueEditText = findViewById(R.id.valueEditText);
        savingsEditText = findViewById(R.id.savingsEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Get the user id passed from the previous activity
        userId = getIntent().getIntExtra("user_id", -1);

        saveButton.setOnClickListener(v -> saveGoal());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void saveGoal() {
        String goalTypeStr = goalTypeEditText.getText().toString().trim();
        String valueStr = valueEditText.getText().toString().trim();
        String savingsStr = savingsEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (goalTypeStr.isEmpty() || valueStr.isEmpty() || savingsStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int goalType;
        try {
            goalType = Integer.parseInt(goalTypeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid goal type", Toast.LENGTH_SHORT).show();
            return;
        }

        double value, savings;
        try {
            value = Double.parseDouble(valueStr);
            savings = Double.parseDouble(savingsStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid numeric values", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the selected date (DatePicker months are 0-indexed, so add 1)
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();

        boolean inserted = insertGoal(userId, goalType, month, day, year, value, savings, description);
        if (inserted) {
            NotificationHelper.createNotificationChannel(this);
            NotificationHelper.sendNotification(
                    this,
                    "Goal Created",
                    "You've successfully created a new goal!"
            );
            Toast.makeText(this, "Goal created", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error creating goal", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean insertGoal(int userId, int goalType, int month, int day, int year, double value, double savings, String description) {
        SQLiteDatabase db = dbControl.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(databaseControl.columnGoalType, goalType);
        values.put(databaseControl.columnGoalMonth, month);
        values.put(databaseControl.columnGoalDay, day);
        values.put(databaseControl.columnGoalYear, year);
        values.put(databaseControl.columnValue, value);
        values.put(databaseControl.columnSavings, savings);
        values.put(databaseControl.columnGoalDesc, description);
        // journal_id and tran_id are optional; we set them as null here
        values.putNull(databaseControl.columnJournalId);
        values.putNull(databaseControl.columnTranId);
        values.put(databaseControl.columnUserId, userId);

        long result = db.insert(databaseControl.goalTable, null, values);
        return result != -1;
    }
}