package com.example.money_manager;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;

public class ViewTransactionsActivity extends AppCompatActivity {
    private databaseControl dbControl;
    private ListView transactionsListView;
    private MaterialToolbar toolbar;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transactions);

        // Bind toolbar and ListView
        toolbar = findViewById(R.id.toolbar);
        transactionsListView = findViewById(R.id.transactionsListView);

        // Initialize database and user
        dbControl = new databaseControl(this);
        userId = getIntent().getIntExtra("user_id", -1);

        // Show current total at top
        double total = dbControl.getTransactionRunningTotal(userId);
        toolbar.setSubtitle(String.format("Current Balance: $%.2f", total));

        // Fetch all transactions and display
        List<String> formatted = new ArrayList<>();
        List<databaseControl.TransactionRecord> records = dbControl.getAllTransactions(userId);
        for (databaseControl.TransactionRecord rec : records) {
            formatted.add(String.format("%04d-%02d-%02d  %8.2f  %s",
                    rec.year, rec.month, rec.day,
                    rec.amount, rec.reason));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                formatted);
        transactionsListView.setAdapter(adapter);
    }
}