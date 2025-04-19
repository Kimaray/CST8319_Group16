package com.example.money_manager;

import android.content.Context;
import java.util.Calendar;

/**
 * Utility to insert test data on first run.
 */
public class TestDataInitializer {

    /**
     * Initializes the database with sample users, journal entries, events, and goals
     * if no users currently exist.
     */
    public static void initialize(Context context) {
        databaseControl db = new databaseControl(context);
        // Check if any users exist
        if (db.getUsers().isEmpty()) {
            // Insert generic users
            db.insertUser("Alice", "alice123");
            db.insertUser("Bob", "bob123");
            db.insertUser("Carol", "carol123");

            // Fetch today’s date
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;  // 1-based
            int day = cal.get(Calendar.DAY_OF_MONTH);

            // For each user, insert a sample journal entry, event, and goal
            for (String username : db.getUsers()) {
                int userId = db.authenticateUser(username, username.toLowerCase() + "123");
                // Sample journal entry
                db.insertJournalEntry(userId, year, month, day, 100.00, "Welcome entry for " + username);
                // Sample event
                db.insertEvent(userId, month, day, year, "Sample Event for " + username);
                // Sample goal
                db.insertGoal(userId, 1, month, day, year, 500.00, 50.00, "Sample Goal for " + username);
            }
        }
    }
}
