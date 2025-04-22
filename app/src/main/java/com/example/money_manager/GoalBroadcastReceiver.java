package com.example.money_manager;

import com.example.money_manager.NotificationUtils;
import com.example.money_manager.GoalBroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GoalBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Handle your goal reminder logic here
        String goalTitle = intent.getStringExtra("goal_title");

        // Show the reminder notification
        NotificationUtils.showNotification(
                context,
                "Goal Reminder",
                "Don't forget your goal: " + goalTitle,
                1003 // Notification ID
        );
    }
}