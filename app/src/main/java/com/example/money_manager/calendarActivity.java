package com.example.money_manager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class calendarActivity extends AppCompatActivity {

    TextView title;
    Button logoutButton, showEventsButton, showGoalsButton, createEventButton, viewJournalButton, createJournalButton, createGoalButton, createTransactionButton;
    int userId;
    databaseControl databaseControl;
    MaterialCalendarView calendarView;
    CalendarDataFetcher dataFetcher;
    int selectedYear = 0;
    int selectedMonth = 0;
    int selectedDay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        // Initialize database and data fetcher
        databaseControl = new databaseControl(this);
        dataFetcher = new CalendarDataFetcher(databaseControl);
        // Get views by ID
        title = findViewById(R.id.title);
        logoutButton = findViewById(R.id.logout);
        showEventsButton = findViewById(R.id.showEvents);
        showGoalsButton = findViewById(R.id.showGoals);
        createEventButton = findViewById(R.id.createEvent);
        viewJournalButton = findViewById(R.id.viewJournal);
        createJournalButton = findViewById(R.id.createJournal);
        createGoalButton = findViewById(R.id.createGoal);
        calendarView = findViewById(R.id.calendarView);
        createTransactionButton = findViewById(R.id.createTransactionButton);


        // Retrieve user ID and update title
        userId = getIntent().getIntExtra("user_id", -1);
        String username = databaseControl.selectUsername(userId);
        title.setText("Welcome to Money Manager " + username + "!");

        calendarView.setOnDateChangedListener((view, date, selected) -> {
            // Updates the chosen date from the user
            selectedYear = date.getYear();
            selectedMonth = date.getMonth() +1;
            selectedDay = date.getDay();
        });




        // Set button listeners
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(calendarActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        showEventsButton.setOnClickListener(v -> {
            Intent intent = new Intent(calendarActivity.this, ViewEventsActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        showGoalsButton.setOnClickListener(v -> {
            Intent intent = new Intent(calendarActivity.this, ViewGoalsActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        createEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(calendarActivity.this, CreateEventActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        viewJournalButton.setOnClickListener(v -> {
            if (selectedYear != 0 && selectedMonth != 0 && selectedDay != 0) {
                Intent intent = new Intent(calendarActivity.this, ViewJournalActivity.class);
                intent.putExtra("user_id", userId);
                intent.putExtra("year", selectedYear);
                intent.putExtra("month", selectedMonth);
                intent.putExtra("day", selectedDay);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Select a date first", Toast.LENGTH_SHORT).show();
            }
        });

        createJournalButton.setOnClickListener(v -> {
            Intent intent = new Intent(calendarActivity.this, CreateJournalActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        createGoalButton.setOnClickListener(v -> {
            Intent intent = new Intent(calendarActivity.this, CreateGoalActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });
        createTransactionButton.setOnClickListener(v -> {
            Intent intent = new Intent(calendarActivity.this, CreateTransactionActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        // Adjust window insets for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initial load of decorators
        refreshCalendarDecorators();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh decorators when returning to the calendar
        refreshCalendarDecorators();
    }

    /**
     * Refreshes the calendar decorators by building a map of CalendarDay to dot colors
     * and then adding a DayDotDecorator for each day.
     */
    private void refreshCalendarDecorators() {
        // Remove existing decorators
        calendarView.removeDecorators();

        // Build a mapping of CalendarDay to a list of dot colors
        Map<CalendarDay, java.util.List<Integer>> dayColorMap = new HashMap<>();

        // Fetch dates from the database
        List<CalendarDay> journalDays = dataFetcher.getJournalDays(userId);
        List<CalendarDay> eventDays = dataFetcher.getEventDays(userId);
        List<CalendarDay> goalDays = dataFetcher.getGoalDays(userId);

        // For journal entries, add blue dots
        for (CalendarDay day : journalDays) {
            addColorForDay(dayColorMap, day, Color.BLUE);
        }
        // For events, add green dots
        for (CalendarDay day : eventDays) {
            addColorForDay(dayColorMap, day, Color.GREEN);
        }
        // For goals, add red dots
        for (CalendarDay day : goalDays) {
            addColorForDay(dayColorMap, day, Color.RED);
        }

        // For each day with dots, create a decorator and add it to the calendar
        for (Map.Entry<CalendarDay, java.util.List<Integer>> entry : dayColorMap.entrySet()) {
            CalendarDay day = entry.getKey();
            java.util.List<Integer> colorList = entry.getValue();
            // Convert List<Integer> to int[]
            int[] colors = new int[colorList.size()];
            for (int i = 0; i < colorList.size(); i++) {
                colors[i] = colorList.get(i);
            }
            // Create a decorator for this day
            DayDotDecorator decorator = new DayDotDecorator(day, colors, 5f);
            calendarView.addDecorator(decorator);
        }

        calendarView.invalidateDecorators();
    }

    /**
     * Helper method to add a color for a given day into the map.
     */
    private void addColorForDay(Map<CalendarDay, java.util.List<Integer>> map, CalendarDay day, int color) {
        if (!map.containsKey(day)) {
            map.put(day, new java.util.ArrayList<>());
        }
        map.get(day).add(color);
    }
}
