package com.example.money_manager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CalendarActivity extends AppCompatActivity {
    TextView title;
    Button logoutButton;
    int userId;
    databaseControl databaseControl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);
        databaseControl = new databaseControl(this);
        title = findViewById(R.id.title);
        logoutButton = findViewById(R.id.logout);
        userId = getIntent().getIntExtra("user_id",-1);
        String username = databaseControl.selectUsername(userId);
        title.setText("Welcome to Money Manager " + username +"!");

        logoutButton.setOnClickListener(v -> { //This is the listener for the logout button
            Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Finishes off anything in this activity before proceeding back to login screen
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}