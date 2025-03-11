package com.example.money_manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class databaseControl extends SQLiteOpenHelper {
        private static final String databaseName = "database.db";
        private static final int databaseVersion = 1;


        //Constraining tables and columns for users
        public static final String userTable = "users";
        public static final String columnUserId = "id";
        public static final String columnUsername = "username";
        public static final String columnPassword = "password";

    private static final String createUserTable =
            "CREATE TABLE IF NOT EXISTS " + userTable + " (" +
                    columnUserId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    columnUsername + " TEXT NOT NULL, " +
                    columnPassword + " TEXT NOT NULL);";
        public databaseControl(Context context) {
            super (context, databaseName, null, databaseVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(createUserTable); //Creates tables if they don't exist
            Log.d("databaseControl", "Database Created and table" + userTable + "created");
            System.out.println("SQL onCreate() called");
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
}
