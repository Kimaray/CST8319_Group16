package com.example.money_manager;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class calendarActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "today_reminders";
    private static final int REQ_NOTIF = 1001;

    private TextView title;
    private Button logoutButton, showEventsButton, showGoalsButton,
            createEventButton, viewJournalButton, createJournalButton,
            createGoalButton, createTransactionButton;
    private int userId;
    private databaseControl databaseControl;
    private MaterialCalendarView calendarView;
    private CalendarDataFetcher dataFetcher;
    private int selectedYear = 0;
    private int selectedMonth = 0;
    private int selectedDay = 0;



    TextView currentDateTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        databaseControl = new databaseControl(this);
        dataFetcher = new CalendarDataFetcher(databaseControl);

        title = findViewById(R.id.title);
        logoutButton = findViewById(R.id.logout);
        showEventsButton = findViewById(R.id.showEvents);
        showGoalsButton = findViewById(R.id.showGoals);
        createEventButton = findViewById(R.id.createEvent);
       // viewJournalButton = findViewById(R.id.viewJournal);
       // createJournalButton = findViewById(R.id.createJournal);
        createGoalButton = findViewById(R.id.createGoal);
        createTransactionButton = findViewById(R.id.createTransactionButton);

        calendarView = findViewById(R.id.calendarView);

        currentDateTextView = findViewById(R.id.currentDateTextView);

        //These are for getting current date
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) +1 ;//Month is 0 indexed
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        String currentDate = "Date is: " + String.format("%02d", currentMonth) +"/" + String.format("%02d",currentDay) + "/" + currentYear;
        currentDateTextView.setText(currentDate);

        userId = getIntent().getIntExtra("user_id", -1);

        requestNotificationPermission();
        createNotificationChannel();
        checkTodayAndNotify();

        updateTitleBalance();

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedYear = date.getYear();
            selectedMonth = date.getMonth() + 1;
            selectedDay = date.getDay();
        });

        setupButtonListeners();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        refreshCalendarDecorators();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTitleBalance();
        refreshCalendarDecorators();
    }

    private void updateTitleBalance() {
        String username = databaseControl.selectUsername(userId);
        double balance = databaseControl.getTransactionRunningTotal(userId);
        String balText = String.format("$%.2f", balance);
        title.setText("Welcome " + username + "! Current Balance: " + balText);
    }

    private void setupButtonListeners() {
        logoutButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        showEventsButton.setOnClickListener(v -> launch(ViewEventsActivity.class));
        showGoalsButton.setOnClickListener(v -> launch(ViewGoalsActivity.class));
        createEventButton.setOnClickListener(v -> launch(CreateEventActivity.class));
       // createJournalButton.setOnClickListener(v -> launch(CreateJournalActivity.class));
        createGoalButton.setOnClickListener(v -> launch(CreateGoalActivity.class));
        createTransactionButton.setOnClickListener(v -> launch(CreateTransactionActivity.class));
        Button viewTransactionsButton = findViewById(R.id.viewTransactions);
        viewTransactionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewTransactionsActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });


      /* viewJournalButton.setOnClickListener(v -> {
            if (selectedYear != 0) {
                Intent i = new Intent(this, viewJournalActivity.class);
                i.putExtra("user_id", userId);
                i.putExtra("year", selectedYear);
                i.putExtra("month", selectedMonth);
                i.putExtra("day", selectedDay);
                startActivity(i);
            } else {
                Toast.makeText(this, "Select a date first", Toast.LENGTH_SHORT).show();
            }
        });
       */
    }

    private void launch(Class<?> cls) {
        Intent i = new Intent(this, cls);
        i.putExtra("user_id", userId);
        startActivity(i);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_NOTIF);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Today's Reminders", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Daily events & goals");
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }

    private void checkTodayAndNotify() {
        Calendar today = Calendar.getInstance();
        int y = today.get(Calendar.YEAR);
        int m = today.get(Calendar.MONTH) + 1;
        int d = today.get(Calendar.DAY_OF_MONTH);

        List<String> events = databaseControl.getEventsOnDate(userId, y, m, d);
        List<String> goals  = databaseControl.getGoalsOnDate(userId, y, m, d);

        if (events.isEmpty() && goals.isEmpty()) return;

        StringBuilder content = new StringBuilder();
        if (!events.isEmpty()) {
            content.append("Events:\n");
            for (String e : events) content.append("• ").append(e).append("\n");
        }
        if (!goals.isEmpty()) {
            content.append("Goals:\n");
            for (String g : goals) content.append("• ").append(g).append("\n");
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Today's Reminders")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content.toString()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(1001, builder.build());
    }

    private void refreshCalendarDecorators() {
        calendarView.removeDecorators();
        Map<CalendarDay, List<Integer>> map = new HashMap<>();

        // Existing data sources
        addColors(map, dataFetcher.getJournalDays(userId), Color.BLUE);
        addColors(map, dataFetcher.getEventDays(userId), Color.GREEN);
        addColors(map, dataFetcher.getGoalDays(userId), Color.RED);

        // New: add transaction dates with magenta dots
        List<CalendarDay> transactionDays = dataFetcher.getTransactionDays(userId);
        addColors(map, transactionDays, Color.MAGENTA);

        // Apply decorators
        for (Map.Entry<CalendarDay, List<Integer>> entry : map.entrySet()) {
            CalendarDay day = entry.getKey();
            List<Integer> colors = entry.getValue();
            int[] dots = new int[colors.size()];
            for (int i = 0; i < colors.size(); i++) {
                dots[i] = colors.get(i);
            }
            calendarView.addDecorator(new DayDotDecorator(day, dots, 5f));
        }
        calendarView.invalidateDecorators();
    }

    private void addColors(Map<CalendarDay, List<Integer>> map, List<CalendarDay> days, int color) {
        for (CalendarDay d : days) map.computeIfAbsent(d, k -> new java.util.ArrayList<>()).add(color);
    }
}