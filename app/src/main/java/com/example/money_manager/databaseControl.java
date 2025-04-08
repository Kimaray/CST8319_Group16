package com.example.money_manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.content.ContentValues;


import java.util.ArrayList;

public class databaseControl extends SQLiteOpenHelper {
    private static final String databaseName = "database.db";
    private static final int databaseVersion = 1;

    //Constraining tables and columns for users
    public static final String userTable = "users";
    public static final String columnUserId = "user_id";
    public static final String columnUsername = "username";
    public static final String columnPassword = "password";

    //Constraining tables and columns for events
    public static final String eventTable = "event";
    public static final String columnEventId = "event_id";
    public static final String columnEventMonth = "event_month";
    public static final String columnEventDay = "event_day";
    public static final String columnEventYear = "event_year";
    public static final String columnEventDetail = "detail";

    //Constraining tables and columns for journal entries
    public static final String journalTable = "journal";
    public static final String columnJournalId = "journal_id";
    public static final String columnJournalMonth= "journal_month";
    public static final String columnJournalDay= "journal_day";
    public static final String columnJournalYear= "journal_year";
    public static final String columnJournalContents = "contents";

    //Constraining tables and columns for transactions
    public static final String transactionTable = "transactions";
    public static final String columnTranId = "tran_id";
    public static final String columnAmount = "amount";
    public static final String columnReason = "reason";
    public static final String columnTranMonth = "tran_month";
    public static final String columnTranDay = "tran_day";
    public static final String columnTranYear = "tran_year";

    //Constraining tables and columns for goals
    public static final String goalTable = "goal";
    public static final String columnGoalId = "goal_id";
    public static final String columnGoalType = "goal_type";
    public static final String columnGoalMonth = "goal_month";
    public static final String columnGoalDay = "goal_day";
    public static final String columnGoalYear= "goal_year";
    public static final String columnValue = "value";
    public static final String columnSavings = "savings";
    public static final String columnGoalDesc = "description";

    private static final String createUserTable =
            "CREATE TABLE IF NOT EXISTS " + userTable + " (" +
                    columnUserId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    columnUsername + " TEXT NOT NULL, " +
                    columnPassword + " TEXT NOT NULL);";

    public static final String createJournalTable =
            "CREATE TABLE IF NOT EXISTS " + journalTable + " (" +
                    columnJournalId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    columnJournalMonth + " INT NOT NULL, " +
                    columnJournalDay + " INT NOT NULL, " +
                    columnJournalYear + " INT NOT NULL, " +
                    columnJournalContents + " TEXT NOT NULL, " +
                    columnUserId + " INTEGER NOT NULL, " +
                    columnEventId + " INTEGER, " +
                    "FOREIGN KEY(" + columnUserId + ") REFERENCES " + userTable + "(" + columnUserId + "), " +
                    "FOREIGN KEY(" + columnEventId + ") REFERENCES " + eventTable + "(" + columnEventId + "));";

    public static final String createEventTable =
            "CREATE TABLE IF NOT EXISTS " + eventTable + " (" +
                    columnEventId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    columnEventMonth + " INT NOT NULL, " +
                    columnEventDay + " INT NOT NULL, " +
                    columnEventYear + " INT NOT NULL, " +
                    columnEventDetail + " TEXT NOT NULL, " +
                    columnUserId + " INTEGER NOT NULL, " +
                    columnTranId + " INTEGER NOT NULL, " +
                    columnJournalId + " INTEGER, " +
                    "FOREIGN KEY(" + columnUserId + ") REFERENCES " + userTable + "(" + columnUserId + "), " +
                    "FOREIGN KEY(" + columnJournalId + ") REFERENCES " + journalTable + "(" + columnJournalId + "), " +
                    "FOREIGN KEY(" + columnTranId + ") REFERENCES " + transactionTable + "(" + columnTranId + "));";

    public static final String createTransactionTable =
            "CREATE TABLE IF NOT EXISTS " + transactionTable + " ("+
                    columnTranId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    columnAmount + " DECIMAL(10,2) NOT NULL, " +
                    columnReason + " TEXT NOT NULL, " +
                    columnTranMonth + " INT NOT NULL, " +
                    columnTranDay + " INT NOT NULL, " +
                    columnTranYear + " INT NOT NULL, " +
                    columnUserId + " INTEGER NOT NULL, " +
                    columnEventId + " INTEGER NOT NULL, " +
                    "FOREIGN KEY(" + columnUserId + ") REFERENCES " + userTable + "(" + columnUserId + "), " +
                    "FOREIGN KEY(" + columnEventId + ") REFERENCES " + eventTable + "(" + columnEventId + "));";

    public static final String createGoalTable =
            "CREATE TABLE IF NOT EXISTS " + goalTable + " (" +
                    columnGoalId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    columnGoalType + " INTEGER NOT NULL, " +
                    columnGoalMonth + " INT NOT NULL, " +
                    columnGoalDay + " INT NOT NULL, " +
                    columnGoalYear + " INT NOT NULL, " +
                    columnValue + " DECIMAL(10,2) NOT NULL, " +
                    columnSavings + " DECIMAL(10,2) NOT NULL, " +
                    columnGoalDesc + " TEXT NOT NULL, " +
                    columnJournalId + " INTEGER, " +
                    columnTranId + " INTEGER, " +
                    columnUserId + " INTEGER NOT NULL, " +
                    "FOREIGN KEY(" + columnUserId + ") REFERENCES " + userTable + "(" + columnUserId + "), " +
                    "FOREIGN KEY(" + columnJournalId + ") REFERENCES " + journalTable + "(" + columnJournalId + "), " +
                    "FOREIGN KEY(" + columnTranId + ") REFERENCES " + transactionTable + "(" + columnTranId + "));";

    // This needs to be enabled to allow foreign key refrences to work
    private static final String keys = "PRAGMA foreign_keys = ON;";
    private static final String record1 = "INSERT INTO users (username,password) values ('John_Smith', 'password123');";
    private static final String record2 = "INSERT INTO users (username,password) values ('Jane_Doe', 'password123');";
    private static final String record3 = "INSERT INTO users (username,password) values ('Mary_Poppins', 'password123');";
    private static final String record4 = "INSERT INTO users (username,password) values ('Bob_Bob', 'password123');";
    public databaseControl(Context context) {
        super (context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(keys);
        db.execSQL(createUserTable); //Creates tables if they don't exist
        db.execSQL(createJournalTable);
        db.execSQL(createEventTable);
        db.execSQL(createTransactionTable);
        db.execSQL(createGoalTable);
        db.execSQL(record1);
        db.execSQL(record2);
        db.execSQL(record3);
        db.execSQL(record4);
        Log.d("databaseControl", "Database Created and table" + userTable + " created");
        Log.d("databaseControl", "Database Created and table" + journalTable + " created");
        Log.d("databaseControl", "Database Created and table" + eventTable + " created");
        Log.d("databaseControl", "Database Created and table" + transactionTable + " created");
        Log.d("databaseControl", "Database Created and table" + goalTable + " created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + userTable); //Drops the old one if it exists
        onCreate(db);
    }
    //Getting only the list of usernames that have been inserted this is for the login screen
    public ArrayList<String>getUsers() {
        ArrayList<String> usernames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        //Executing the select statement here for usernames
        Cursor cursor = db.query(userTable, new String[]{columnUsername}, null, null, null, null, null);
        if (cursor != null) {
            while(cursor.moveToNext()){
                @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex(columnUsername));
                usernames.add(username);
            }
            cursor.close();
        }
        System.out.println("Fetched users: " + usernames);
        return usernames;
    }

    @SuppressLint("Range")
    public int authenticateUser(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT " + columnUserId + " FROM " + userTable +
                " WHERE " + columnUsername + " = ? AND " + columnPassword + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{username, password});
        int userId = -1;
        //If this exists it will move to it and set the value
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(columnUserId));
        }
        cursor.close();
        return userId;
    }
    @SuppressLint("Range")
    public String selectUsername(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + columnUsername + " FROM " + userTable +
                " WHERE " + columnUserId + " = ?";
        String username = null;
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndex(columnUsername));
        }
        cursor.close();
        return username;
    }

    public void insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(columnUsername, username);
        values.put(columnPassword, password);
        long result = db.insert(userTable, null, values);
        if (result == -1) {
            Log.d("databaseControl", "Failed to insert user from insertUser()");
        } else {
            Log.d ("databaseControl", "User inserted successfully");
        }
    }

    // creating the journal entry into the database
    @SuppressLint("Range")
    public void saveJournalEntry(int userId, int year, int month, int day, String contents) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(columnUserId, userId);
        values.put(columnJournalYear, year);
        values.put(columnJournalMonth, month);
        values.put(columnJournalDay, day);
        values.put(columnJournalContents, contents);
        db.insert(journalTable, null, values);
    }

    // Gets the journal entries for the user and date chosen
    @SuppressLint("Range")
    public ArrayList<String> getJournalEntries(int userId, int year, int month, int day) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + columnJournalContents + " FROM " + journalTable +
                " WHERE " + columnUserId + "=? AND " + columnJournalYear + "=? AND " +
                columnJournalMonth + "=? AND " + columnJournalDay + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId), String.valueOf(year), String.valueOf(month), String.valueOf(day)
        });

    // Creating the list with the entries
        ArrayList<String> entries = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String content = cursor.getString(cursor.getColumnIndex(columnJournalContents));
                entries.add(content);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return entries;
    }
}
