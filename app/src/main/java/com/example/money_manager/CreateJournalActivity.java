package com.example.money_manager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class CreateJournalActivity extends AppCompatActivity {
    private databaseControl dbControl;
    private Button dateButton;
    private TextInputEditText contentInput;
    private Button saveButton, backButton;
    private int userId;
    private int selYear, selMonth, selDay;
    private TextInputEditText amountInput, descriptionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journal);

        dbControl = new databaseControl(this);
        amountInput = findViewById(R.id.amountInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        dateButton = findViewById(R.id.dateButton);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);

        userId = getIntent().getIntExtra("user_id", -1);

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

        saveButton.setOnClickListener(v -> {
            String amtStr = amountInput.getText().toString().trim();
            String desc = descriptionInput.getText().toString().trim();
            if (amtStr.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Enter amount and description", Toast.LENGTH_SHORT).show();
                return;
            }
            double amt;
            try {
                amt = Double.parseDouble(amtStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }
            // Insert using new method
            long id = dbControl.insertJournalEntry(userId, selYear, selMonth + 1, selDay, amt, desc);
            if (id > 0) {
                Toast.makeText(this, "Entry saved (ID=" + id + ")", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> {
            Intent back = new Intent(this, calendarActivity.class);
            back.putExtra("user_id", userId);
            startActivity(back);
            finish();
        });
    }

    private void updateDateText() {
        dateButton.setText(String.format("%04d-%02d-%02d", selYear, selMonth + 1, selDay));
    }
}