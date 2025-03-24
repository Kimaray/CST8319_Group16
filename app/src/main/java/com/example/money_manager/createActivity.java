package com.example.money_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class createActivity extends AppCompatActivity {
    Button backButton;
    Button createButton;
    EditText usernameInput;
    EditText passwordInput;
    EditText verifyInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create);
        createButton = findViewById(R.id.createButton);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        verifyInput = findViewById(R.id.verifyInput);
        backButton = findViewById(R.id.backButton);

        createButton.setOnClickListener(v -> {
                    String username = usernameInput.getText().toString();
                    String password = passwordInput.getText().toString();
                    String verify = verifyInput.getText().toString();
                    //Input validation
                    if (username.isEmpty() || password.isEmpty() || verify.isEmpty()) {
                        Toast.makeText(createActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    } else if (!password.equals(verify)) {
                        Toast.makeText(createActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    } else {
                        databaseControl db = new databaseControl(createActivity.this);
                        db.insertUser(username, password);
                        Toast.makeText(createActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                    }
                });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(createActivity.this, MainActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}