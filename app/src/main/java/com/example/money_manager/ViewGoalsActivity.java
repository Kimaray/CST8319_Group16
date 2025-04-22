package com.example.money_manager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ViewGoalsActivity extends AppCompatActivity {

    private databaseControl dbControl;
    private int userId;
    private ListView goalsListView;
    private Button backToCalendarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_goals);

        dbControl = new databaseControl(this);
        goalsListView = findViewById(R.id.goalsListView);
        backToCalendarButton = findViewById(R.id.backToCalendarButton);

        // Get the user id passed from the previous activity
        userId = getIntent().getIntExtra("user_id", -1);

        // Query the goals for the current user
        ArrayList<String> goals = getGoalsForUser(userId);

        if (goals.isEmpty()) {
            Toast.makeText(this, "No goals found", Toast.LENGTH_SHORT).show();
        }

        // Use a simple ArrayAdapter to display the goals
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, goals);
        goalsListView.setAdapter(adapter);

        backToCalendarButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(ViewGoalsActivity.this, calendarActivity.class);
            backIntent.putExtra("user_id", userId);
            startActivity(backIntent);
            finish();
        });
    }

    private ArrayList<String> getGoalsForUser(int userId) {
        ArrayList<String> goals = new ArrayList<>();
        SQLiteDatabase db = dbControl.getReadableDatabase();

        // Query the goal table for month, day, year, value, savings, description and status for the current user
        String query = "SELECT " + databaseControl.columnGoalMonth + ", " +
                databaseControl.columnGoalDay + ", " +
                databaseControl.columnGoalYear + ", " +
                databaseControl.columnValue + ", " +
                databaseControl.columnSavings + ", " +
                databaseControl.columnGoalDesc + ", " +
                databaseControl.columnGoalStatus +
                " FROM " + databaseControl.goalTable +
                " WHERE " + databaseControl.columnUserId + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int month = cursor.getInt(cursor.getColumnIndex(databaseControl.columnGoalMonth));
                @SuppressLint("Range") int day = cursor.getInt(cursor.getColumnIndex(databaseControl.columnGoalDay));
                @SuppressLint("Range") int year = cursor.getInt(cursor.getColumnIndex(databaseControl.columnGoalYear));
                @SuppressLint("Range") double value = cursor.getDouble(cursor.getColumnIndex(databaseControl.columnValue));
                @SuppressLint("Range") double savings = cursor.getDouble(cursor.getColumnIndex(databaseControl.columnSavings));
                @SuppressLint("Range") String desc = cursor.getString(cursor.getColumnIndex(databaseControl.columnGoalDesc));
                @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(databaseControl.columnGoalStatus));

                String statusText = (status ==1) ? "Complete" : "Incomplete";

                String goal = "Date: " + month + "/" + day + "/" + year +
                        "\nTarget: $" + value +
                        "\nSavings: $" + savings +
                        "\n" + desc +
                        "\n" + statusText;
                goals.add(goal);
            }
            cursor.close();
        }
        return goals;

    }
}
