package com.example.money_manager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {

    private databaseControl dbControl;
    private Button dateButton;
    private TextInputEditText detailInput;
    private Button saveButton, cancelButton;
    private int userId;
    private int selYear, selMonth, selDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        userId     = getIntent().getIntExtra("user_id", -1);
        dbControl  = new databaseControl(this);

        dateButton = findViewById(R.id.dateButton);
        detailInput= findViewById(R.id.eventDetailInput);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        // initialize to today
        Calendar c = Calendar.getInstance();
        selYear = c.get(Calendar.YEAR);
        selMonth = c.get(Calendar.MONTH);
        selDay = c.get(Calendar.DAY_OF_MONTH);
        updateDateButtonText();

        dateButton.setOnClickListener(v -> {
            new DatePickerDialog(this,
                    (DatePicker dp, int y, int m, int d) -> {
                        selYear = y; selMonth = m; selDay = d;
                        updateDateButtonText();
                    },
                    selYear, selMonth, selDay)
                    .show();
        });

        saveButton.setOnClickListener(v -> {
            String detail = detailInput.getText().toString().trim();
            if (detail.isEmpty()) {
                Toast.makeText(this, "Please enter event details", Toast.LENGTH_SHORT).show();
                return;
            }
            long id = dbControl.insertEvent(userId,
                    selMonth+1, selDay, selYear, detail);
            if (id>0) {
                Toast.makeText(this, "Event saved (ID="+id+")", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save event", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> finish());
    }

    private void updateDateButtonText() {
        dateButton.setText(
                String.format("%04d-%02d-%02d", selYear, selMonth+1, selDay));
    }
}
