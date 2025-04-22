package com.example.money_manager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import java.util.List;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class databaseControl extends SQLiteOpenHelper {
    private static final String databaseName = "database.db";
    private static final int databaseVersion = 2;

    // User table
    public static final String userTable = "users";
    public static final String columnUserId = "user_id";
    public static final String columnUsername = "username";
    public static final String columnPassword = "password";

    // Event table
    public static final String eventTable = "event";
    public static final String columnEventId = "event_id";
    public static final String columnEventMonth = "event_month";
    public static final String columnEventDay = "event_day";
    public static final String columnEventYear = "event_year";
    public static final String columnEventDetail = "detail";
    public static final String columnUserIdFk = "user_id";
    public static final String columnTranId = "tran_id";
    public static final String columnJournalId = "journal_id";

    // Journal table
    public static final String journalTable = "journal";

    public static final String columnJournalMonth = "journal_month";
    public static final String columnJournalDay = "journal_day";
    public static final String columnJournalYear = "journal_year";
    public static final String columnJournalAmount = "amount";
    public static final String columnJournalContents = "contents";

    // Transaction table
    public static final String transactionTable = "transactions";
    public static final String columnTranIdPk = "tran_id";
    public static final String columnAmount = "amount";
    public static final String columnReason = "reason";
    public static final String columnTranMonth = "tran_month";
    public static final String columnTranDay = "tran_day";
    public static final String columnTranYear = "tran_year";
    public static final String columnGoalIdFk = "goal_id";

    // Goal table
    public static final String goalTable = "goal";
    public static final String columnGoalId = "goal_id";
    public static final String columnGoalType = "goal_type";
    public static final String columnGoalMonth = "goal_month";
    public static final String columnGoalDay = "goal_day";
    public static final String columnGoalYear = "goal_year";
    public static final String columnValue = "value";
    public static final String columnSavings = "savings";
    public static final String columnGoalDesc = "description";
    public static final String columnGoalStatus = "status";

    // Enable foreign keys
    private static final String keys = "PRAGMA foreign_keys = ON;";
    // Create statements
    private static final String createUserTable =
            "CREATE TABLE IF NOT EXISTS " + userTable + " (" +
                    columnUserId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    columnUsername + " TEXT NOT NULL, " +
                    columnPassword + " TEXT NOT NULL);";

    private static final String createJournalTable =
            "CREATE TABLE IF NOT EXISTS " + journalTable + " (" +
                    columnJournalId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    columnJournalYear + " INT NOT NULL, " +
                    columnJournalMonth + " INT NOT NULL, " +
                    columnJournalDay + " INT NOT NULL, " +
                    columnJournalAmount + " DECIMAL(10,2) NOT NULL, " +
                    columnJournalContents + " TEXT NOT NULL, " +
                    columnUserIdFk + " INTEGER NOT NULL, " +
                    "FOREIGN KEY(" + columnUserIdFk + ") REFERENCES " + userTable + "(" + columnUserId + "));";

    private static final String createEventTable =
            "CREATE TABLE IF NOT EXISTS " + eventTable + " (" +
                    columnEventId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    columnEventYear + " INT NOT NULL, " +
                    columnEventMonth + " INT NOT NULL, " +
                    columnEventDay + " INT NOT NULL, " +
                    columnEventDetail + " TEXT NOT NULL, " +
                    columnUserIdFk + " INTEGER NOT NULL, " +
                    columnTranId + " INTEGER NOT NULL, " +
                    columnJournalId + " INTEGER, " +
                    "FOREIGN KEY(" + columnUserIdFk + ") REFERENCES " + userTable + "(" + columnUserId + "), " +
                    "FOREIGN KEY(" + columnJournalId + ") REFERENCES " + journalTable + "(" + columnJournalId + "));";

    private static final String createTransactionTable =
            "CREATE TABLE IF NOT EXISTS " + transactionTable + " (" +
                    columnTranIdPk + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    columnAmount + " DECIMAL(10,2) NOT NULL, " +
                    columnReason + " TEXT NOT NULL, " +
                    columnTranMonth + " INT NOT NULL, " +
                    columnTranDay + " INT NOT NULL, " +
                    columnTranYear + " INT NOT NULL, " +
                    columnUserIdFk + " INTEGER NOT NULL, " +
                    columnGoalIdFk + " INTEGER," +
                    "FOREIGN KEY(" + columnGoalIdFk + ") REFERENCES " + goalTable + "(" +columnGoalId +")," +
                    "FOREIGN KEY(" + columnUserIdFk + ") REFERENCES " + userTable + "(" + columnUserId + "));";

    private static final String createGoalTable =
            "CREATE TABLE IF NOT EXISTS " + goalTable + " (" +
                    columnGoalId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    columnGoalType + " INTEGER NOT NULL, " +
                    columnGoalYear + " INT NOT NULL, " +
                    columnGoalMonth + " INT NOT NULL, " +
                    columnGoalDay + " INT NOT NULL, " +
                    columnValue + " DECIMAL(10,2) NOT NULL, " +
                    columnSavings + " DECIMAL(10,2) NOT NULL, " +
                    columnGoalDesc + " TEXT NOT NULL, " +
                    columnUserIdFk + " INTEGER NOT NULL, " +
                    columnGoalStatus +" INTEGER NOT NULL,"+ //0 for incomplete 1 for complete
                    "FOREIGN KEY(" + columnUserIdFk + ") REFERENCES " + userTable + "(" + columnUserId + "));";

    public databaseControl(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(keys);
        db.execSQL(createUserTable);
        db.execSQL(createJournalTable);
        db.execSQL(createEventTable);
        db.execSQL(createTransactionTable);
        db.execSQL(createGoalTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        if (oldV < 2) {
            // migrate journal to include amount
            db.execSQL("DROP TABLE IF EXISTS " + journalTable);
        }
        db.execSQL("DROP TABLE IF EXISTS " + userTable);
        db.execSQL("DROP TABLE IF EXISTS " + eventTable);
        db.execSQL("DROP TABLE IF EXISTS " + transactionTable);
        db.execSQL("DROP TABLE IF EXISTS " + goalTable);
        onCreate(db);
    }

    /**
     * Insert a new user
     */
    public void insertUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(columnUsername, username);
        v.put(columnPassword, password);
        db.insert(userTable, null, v);
    }

    /**
     * Authenticate user
     */
    @SuppressLint("Range")
    public int authenticateUser(String uname, String pwd) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + columnUserId + " FROM " + userTable +
                        " WHERE " + columnUsername + "=? AND " + columnPassword + "=?",
                new String[]{uname, pwd});
        int id = -1;
        if (c.moveToFirst()) id = c.getInt(c.getColumnIndex(columnUserId));
        c.close();
        return id;
    }

    /**
     * Select username by ID
     */
    @SuppressLint("Range")
    public String selectUsername(int uid) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + columnUsername + " FROM " + userTable +
                        " WHERE " + columnUserId + "=?",
                new String[]{String.valueOf(uid)});
        String name = null;
        if (c.moveToFirst()) name = c.getString(c.getColumnIndex(columnUsername));
        c.close();
        return name;
    }

    /**
     * Get all usernames
     */
    @SuppressLint("Range")
    public ArrayList<String> getUsers() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(userTable, new String[]{columnUsername}, null, null, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                list.add(c.getString(c.getColumnIndex(columnUsername)));
            }
            c.close();
        }
        return list;
    }

    /**
     * Insert journal entry and return row ID
     */
    public long insertJournalEntry(int uid, int year, int month, int day, double amount, String contents) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(columnJournalYear, year);
        v.put(columnJournalMonth, month);
        v.put(columnJournalDay, day);
        v.put(columnJournalAmount, amount);
        v.put(columnJournalContents, contents);
        v.put(columnUserIdFk, uid);
        return db.insert(journalTable, null, v);
    }

    /**
     * Get journal entries for specific date
     */
    @SuppressLint("Range")
    public List<String> getJournalEntries(int uid, int year, int month, int day) {
        List<String> entries = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + columnJournalAmount + "," + columnJournalContents + " FROM " + journalTable +
                        " WHERE " + columnUserIdFk + "=? AND " + columnJournalYear + "=? AND " + columnJournalMonth + "=? AND " + columnJournalDay + "=?",
                new String[]{String.valueOf(uid), String.valueOf(year), String.valueOf(month), String.valueOf(day)});
        while (c.moveToNext()) {
            double amt = c.getDouble(0);
            String txt = c.getString(1);
            entries.add(String.format("$%.2f — %s", amt, txt));
        }
        c.close();
        return entries;
    }

    /**
     * Calculate running total of journal amounts
     */
    public double getJournalRunningTotal(int uid) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT SUM(" + columnJournalAmount + ") FROM " + journalTable +
                        " WHERE " + columnUserIdFk + "=?",
                new String[]{String.valueOf(uid)});
        double total = 0;
        if (c.moveToFirst()) total = c.getDouble(0);
        c.close();
        return total;
    }

    /**
     * Fetch events on a date
     */
    @SuppressLint("Range")
    public List<String> getEventsOnDate(int uid, int year, int month, int day) {
        List<String> events = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + columnEventDetail + " FROM " + eventTable +
                        " WHERE " + columnUserIdFk + "=? AND " + columnEventYear + "=? AND " + columnEventMonth + "=? AND " + columnEventDay + "=?",
                new String[]{String.valueOf(uid), String.valueOf(year), String.valueOf(month), String.valueOf(day)});
        while (c.moveToNext()) events.add(c.getString(c.getColumnIndex(columnEventDetail)));
        c.close();
        return events;
    }

    public Map<Integer, String> getUserGoals(int userId){
        SQLiteDatabase db = getReadableDatabase();
        Map<Integer, String> goals = new HashMap<>();
        String query = "SELECT " + databaseControl.columnGoalId + ", " +
                databaseControl.columnGoalDesc +
                " FROM " + databaseControl.goalTable +
                " WHERE " + databaseControl.columnUserIdFk + " = ? AND " + columnGoalStatus +"= 0";
        Cursor cursor= db.rawQuery(query, new String[] { String.valueOf(userId) });
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int goalId = cursor.getInt(cursor.getColumnIndex(databaseControl.columnGoalId));
                @SuppressLint("Range") String goalDesc = cursor.getString(cursor.getColumnIndex(databaseControl.columnGoalDesc));

                goals.put(goalId, goalDesc);
            }
            cursor.close();
        }
        db.close();
        return goals;
    }

    /**
     * Fetch goals on a date
     */
    @SuppressLint("Range")
    public List<String> getGoalsOnDate(int uid, int year, int month, int day) {
        List<String> goals = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + columnGoalDesc + " FROM " + goalTable +
                        " WHERE " + columnUserIdFk + "=? AND " + columnGoalYear + "=? AND " + columnGoalMonth + "=? AND " + columnGoalDay + "=?",
                new String[]{String.valueOf(uid), String.valueOf(year), String.valueOf(month), String.valueOf(day)});
        while (c.moveToNext()) goals.add(c.getString(c.getColumnIndex(columnGoalDesc)));
        c.close();
        return goals;
    }

    /**
     * Insert event and return its ID
     */
    public long insertEvent(int uid, int month, int day, int year, String detail) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(columnEventMonth, month);
        v.put(columnEventDay, day);
        v.put(columnEventYear, year);
        v.put(columnEventDetail, detail);
        v.put(columnUserIdFk, uid);
        v.put(columnTranId, 0);
        return db.insert(eventTable, null, v);
    }

    /**
     * Insert goal and return its ID
     */
    public long insertGoal(int uid, int goalType, int month, int day, int year, double value, double savings, String description, int status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(columnGoalType, goalType);
        v.put(columnGoalMonth, month);
        v.put(columnGoalDay, day);
        v.put(columnGoalYear, year);
        v.put(columnValue, value);
        v.put(columnSavings, savings);
        v.put(columnGoalDesc, description);
        v.put(columnUserIdFk, uid);
        v.put(columnGoalStatus, status);
        return db.insert(goalTable, null, v);
    }
    public long insertTransaction(int uid, int year, int month, int day, double amount, String reason) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(columnTranYear, year);
        v.put(columnTranMonth, month);
        v.put(columnTranDay, day);
        v.put(columnAmount, amount);
        v.put(columnReason, reason);
        v.put(columnUserIdFk, uid);
        return db.insert(transactionTable, null, v);
    }
    @SuppressLint("Range")
    public double getTransactionRunningTotal(int uid) {
        SQLiteDatabase db = getReadableDatabase();
        // Sum up the “amount” column (expenses stored as negative, income as positive)
        Cursor c = db.rawQuery(
                "SELECT SUM(" + columnAmount + ") FROM " + transactionTable +
                        " WHERE " + columnUserIdFk + "=?",
                new String[]{ String.valueOf(uid) }
        );
        double total = 0;
        if (c.moveToFirst()) {
            total = c.getDouble(0);
        }
        c.close();
        return total;
    }
    public static class TransactionRecord {
        public final int year, month, day;
        public final double amount;
        public final String reason;

        public TransactionRecord(int y, int m, int d, double amt, String rsn) {
            year = y; month = m; day = d;
            amount = amt; reason = rsn;
        }
    }

    /**
     * Fetch all transactions for the given user, most recent first.
     */
    @SuppressLint("Range")
    public List<TransactionRecord> getAllTransactions(int uid) {
        List<TransactionRecord> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + columnTranYear + "," + columnTranMonth + "," + columnTranDay + "," +
                        columnAmount + "," + columnReason +
                        " FROM " + transactionTable +
                        " WHERE " + columnUserIdFk + "=?"
                        + " ORDER BY " + columnTranYear + " DESC, "
                        + columnTranMonth + " DESC, " + columnTranDay + " DESC",
                new String[]{ String.valueOf(uid) }
        );
        while (c.moveToNext()) {
            int y = c.getInt(0);
            int m = c.getInt(1);
            int d = c.getInt(2);
            double amt = c.getDouble(3);
            String rsn = c.getString(4);
            list.add(new TransactionRecord(y, m, d, amt, rsn));
        }
        c.close();
        return list;
    }

    public void updateStatus(int goalId){
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + columnSavings + ", " + columnValue + " FROM " + goalTable +
                        " WHERE " + columnGoalId + " = ?",
                new String[]{ String.valueOf(goalId) }
        );
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") double savings = cursor.getDouble(cursor.getColumnIndex(columnSavings));
            @SuppressLint("Range") double value = cursor.getDouble(cursor.getColumnIndex(columnValue));

            if (savings >= value) {
                ContentValues values = new ContentValues();
                values.put(columnGoalStatus, 1);
                db.update(goalTable, values, columnGoalId + "=?", new String[] { String.valueOf(goalId)});
            }
        }
        cursor.close();
    }

    public List<Goal> getGoals(int userId) {
        List<Goal> goals = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + columnGoalDay + ", " + columnGoalMonth + ", " + columnGoalYear +
                        " FROM " + goalTable + " WHERE " + columnUserId + " = ?",
                new String[]{String.valueOf(userId)}
        );
        if (cursor.moveToFirst()) {
            do {
                int day = cursor.getInt(0);
                int month = cursor.getInt(1);
                int year = cursor.getInt(2);
                goals.add(new Goal(day, month, year));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return goals;
    }


}