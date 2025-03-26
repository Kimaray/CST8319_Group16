package com.example.money_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class calendarActivity extends AppCompatActivity {

    TextView title;
    Button logoutButton;
    int userId;
    databaseControl databaseControl;
    CalendarView calendar;
    TextView calendarDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);
        databaseControl = new databaseControl(this);
        title = findViewById(R.id.title);
        calendar = findViewById(R.id.calendar);
        logoutButton = findViewById(R.id.logout);
        calendarDateText = findViewById(R.id.calendarText);
        userId = getIntent().getIntExtra("user_id",-1);
        String username = databaseControl.selectUsername(userId);
        title.setText("Welcome to Money Manager " + username +"!");

        logoutButton.setOnClickListener(v -> { //This is the listener for the logout button
            Intent intent = new Intent(calendarActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Finishes off anything in this activity before proceeding back to login screen
        });

        calendar.setOnDateChangeListener((view, year, month, day) -> {
            int processedMonth = properMonth(month);
            calendarDateText.setText("Year: " + year + ", Month: " + processedMonth +", Day: " + day );
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private int properMonth(int month){
        //Have to add 1 since month starts at 0
        month = month+1;
        return month;
    }


}