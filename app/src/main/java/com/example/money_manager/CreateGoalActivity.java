package com.example.money_manager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class CreateGoalActivity extends AppCompatActivity {
    private databaseControl dbControl;
    private TextInputEditText goalInput;
    private TextInputEditText descriptionInput;
    private Button saveButton;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use the original XML layout
        setContentView(R.layout.activity_create_goal);

        // Get user and db
        userId = getIntent().getIntExtra("user_id", -1);
        dbControl = new databaseControl(this);

        // Bind views (original IDs in activity_create_goal.xml)
        goalInput = findViewById(R.id.goalInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> saveGoal());

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.createGoalLayout), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }

    private void saveGoal() {
        String goalStr = goalInput.getText().toString().trim();
        String desc = descriptionInput.getText().toString().trim();
        if (goalStr.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Please enter a goal and description", Toast.LENGTH_SHORT).show();
            return;
        }
        double target;
        try {
            target = Double.parseDouble(goalStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid goal amount", Toast.LENGTH_SHORT).show();
            return;
        }
        // Auto-calc current savings
        double currentSavings = dbControl.getTransactionRunningTotal(userId);
        // Today’s date
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        long id = dbControl.insertGoal(
                userId,
                0, // type unused
                month,
                day,
                year,
                target,
                currentSavings,
                desc,
                0 //This is to set the goalstatus as 0 by default
        );
        if (id > 0) {
            Toast.makeText(this, "Goal saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving goal", Toast.LENGTH_SHORT).show();
        }
    }
}