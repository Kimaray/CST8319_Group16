package com.example.money_manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.money_manager.NotificationUtils;
import com.example.money_manager.GoalBroadcastReceiver;

public class GoalDeadlineReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String goalTitle = intent.getStringExtra("goalTitle");

        NotificationUtils.showNotification(
                context,
                "Goal Deadline Approaching!",
                "Your goal \"" + goalTitle + "\" is due soon!",
                1002
        );
    }
    private void scheduleGoalReminder(Context context, String goalTitle, long deadlineMillis) {
        // Make sure GoalBroadcastReceiver exists in the same package
        Intent intent = new Intent(context, GoalBroadcastReceiver.class);
        intent.putExtra("goal_title", goalTitle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long reminderTime = deadlineMillis - 24 * 60 * 60 * 1000; // 1 day before
        alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
    }


}