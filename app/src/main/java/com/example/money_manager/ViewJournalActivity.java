package com.example.money_manager;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ViewJournalActivity extends AppCompatActivity {

    private databaseControl dbControl;
    private int userId;
    private ListView journalListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_journal);

        dbControl = new databaseControl(this);
        journalListView = findViewById(R.id.journalListView);

        // Retrieve the user_id passed from the previous activity
        userId = getIntent().getIntExtra("user_id", -1);

        // Get all journal entries for this user
        ArrayList<String> journalEntries = getJournalEntries(userId);

        if (journalEntries.isEmpty()) {
            Toast.makeText(this, "No journal entries found", Toast.LENGTH_SHORT).show();
        }

        // Display the journal entries in a ListView using a simple ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, journalEntries);
        journalListView.setAdapter(adapter);
    }

    /**
     * Retrieves all journal entries for the given user.
     */
    private ArrayList<String> getJournalEntries(int userId) {
        ArrayList<String> entries = new ArrayList<>();
        SQLiteDatabase db = dbControl.getReadableDatabase();

        // Query for the journal month, day, year, and contents for the current user
        String query = "SELECT " + databaseControl.columnJournalMonth + ", " +
                databaseControl.columnJournalDay + ", " +
                databaseControl.columnJournalYear + ", " +
                databaseControl.columnJournalContents +
                " FROM " + databaseControl.journalTable +
                " WHERE " + databaseControl.columnUserId + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int month = cursor.getInt(cursor.getColumnIndex(databaseControl.columnJournalMonth));
                @SuppressLint("Range") int day = cursor.getInt(cursor.getColumnIndex(databaseControl.columnJournalDay));
                @SuppressLint("Range") int year = cursor.getInt(cursor.getColumnIndex(databaseControl.columnJournalYear));
                @SuppressLint("Range") String contents = cursor.getString(cursor.getColumnIndex(databaseControl.columnJournalContents));

                // Format the journal entry for display
                String entry = "Date: " + month + "/" + day + "/" + year + "\n" + contents;
                entries.add(entry);
            }
            cursor.close();
        }
        return entries;
    }
}
