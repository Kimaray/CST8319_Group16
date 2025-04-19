package com.example.money_manager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.List;

public class CalendarDataFetcher {

    private final databaseControl dbControl;

    public CalendarDataFetcher(databaseControl dbControl) {
        this.dbControl = dbControl;
    }

    public List<CalendarDay> getJournalDays(int userId) {
        List<CalendarDay> days = new ArrayList<>();
        SQLiteDatabase db = dbControl.getReadableDatabase();

        String query = "SELECT " +
                databaseControl.columnJournalYear + ", " +
                databaseControl.columnJournalMonth + ", " +
                databaseControl.columnJournalDay +
                " FROM " + databaseControl.journalTable +
                " WHERE " + databaseControl.columnUserId + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int year = cursor.getInt(cursor.getColumnIndex(databaseControl.columnJournalYear));
                int month = cursor.getInt(cursor.getColumnIndex(databaseControl.columnJournalMonth));
                int day = cursor.getInt(cursor.getColumnIndex(databaseControl.columnJournalDay));
                // Subtract one from month so that if stored month is 1 for January,
                // CalendarDay.from(year, 0, day) will display January correctly.
                CalendarDay calendarDay = CalendarDay.from(year, month - 1, day);
                days.add(calendarDay);
            }
            cursor.close();
        }
        return days;
    }

    public List<CalendarDay> getEventDays(int userId) {
        List<CalendarDay> days = new ArrayList<>();
        SQLiteDatabase db = dbControl.getReadableDatabase();

        String query = "SELECT " +
                databaseControl.columnEventYear + ", " +
                databaseControl.columnEventMonth + ", " +
                databaseControl.columnEventDay +
                " FROM " + databaseControl.eventTable +
                " WHERE " + databaseControl.columnUserId + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int year = cursor.getInt(cursor.getColumnIndex(databaseControl.columnEventYear));
                int month = cursor.getInt(cursor.getColumnIndex(databaseControl.columnEventMonth));
                int day = cursor.getInt(cursor.getColumnIndex(databaseControl.columnEventDay));
                CalendarDay calendarDay = CalendarDay.from(year, month - 1, day);
                days.add(calendarDay);
            }
            cursor.close();
        }
        return days;
    }

    public List<CalendarDay> getGoalDays(int userId) {
        List<CalendarDay> days = new ArrayList<>();
        SQLiteDatabase db = dbControl.getReadableDatabase();

        String query = "SELECT " +
                databaseControl.columnGoalYear + ", " +
                databaseControl.columnGoalMonth + ", " +
                databaseControl.columnGoalDay +
                " FROM " + databaseControl.goalTable +
                " WHERE " + databaseControl.columnUserId + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int year = cursor.getInt(cursor.getColumnIndex(databaseControl.columnGoalYear));
                int month = cursor.getInt(cursor.getColumnIndex(databaseControl.columnGoalMonth));
                int day = cursor.getInt(cursor.getColumnIndex(databaseControl.columnGoalDay));
                CalendarDay calendarDay = CalendarDay.from(year, month - 1, day);
                days.add(calendarDay);
            }
            cursor.close();
        }
        return days;
    }

    public List<CalendarDay> getTransactionDays(int userId) {
        List<CalendarDay> days = new ArrayList<>();
        Cursor c = dbControl.getReadableDatabase().rawQuery(
                "SELECT " + databaseControl.columnTranYear + ", " + databaseControl.columnTranMonth + ", " + databaseControl.columnTranDay +
                        " FROM " + databaseControl.transactionTable +
                        " WHERE " + databaseControl.columnUserIdFk + "=?",
                new String[]{ String.valueOf(userId) }
        );
        while (c.moveToNext()) {
            int y = c.getInt(0);
            int m = c.getInt(1);
            int d = c.getInt(2);
            // CalendarDay expects month 0-based, so subtract 1
            days.add(CalendarDay.from(y, m - 1, d));
        }
        c.close();
        return days;
    }
}
