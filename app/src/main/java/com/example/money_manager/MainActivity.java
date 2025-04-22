package com.example.money_manager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    databaseControl databaseControl;
    TextView usernameTextView;
    TextView profileTextView;
    ArrayList<String> usernames;
    int currentIndex = 0;
    Button previousButton;
    Button nextButton;
    Button loginButton;
    Button shutdownButton;
    Button createAccountButton;
    EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        TestDataInitializer.initialize(this);

        loginButton = findViewById(R.id.loginbutton);
        shutdownButton = findViewById(R.id.shutdown);
        databaseControl = new databaseControl(this);
        usernameTextView = findViewById(R.id.usernameView);
        profileTextView = findViewById(R.id.profileText);
        previousButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        passwordInput = findViewById(R.id.passwordInput);
        createAccountButton = findViewById(R.id.createAccountButton);
        usernames = databaseControl.getUsers();
        Log.d("Main Activity", "Fetched Usernames:" + usernames);


        //This will set the username textfield with the first selected result from the getusers method
        if (usernames.size() > 0){
            displayUser(currentIndex);
        } else {
            usernameTextView.setText("No users have been created");
        }


        previousButton.setOnClickListener(v ->{
                if (usernames.size() > 0 ) {
                    currentIndex = (currentIndex - 1 + usernames.size()) % usernames.size();
                    displayUser(currentIndex);
                }
        });

        nextButton.setOnClickListener(v -> {
                if (usernames.size() > 0 ) {
                    currentIndex = (currentIndex + 1) % usernames.size();
                    displayUser(currentIndex);
                }
        });

        loginButton.setOnClickListener(v -> {
            String username = usernameTextView.getText().toString();
            String password = passwordInput.getText().toString();
            int userId = databaseControl.authenticateUser(username, password);
            if (userId != -1) { //-1 is the default value and if it doesn't change it means nothing was returned from the select
                Intent intent = new Intent(MainActivity.this, calendarActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
                finish(); // Finishes this activity
            } else {
                Toast.makeText(MainActivity.this, "Invalid Password!", Toast.LENGTH_SHORT).show();
            }

        });

        createAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, createActivity.class);
            startActivity(intent);
        });

        shutdownButton.setOnClickListener(v -> {
            finishAffinity(); //Closes everything in the task
            System.exit(0); // Exits the application
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // This will get the username at the current index, set it to the username parameter and set the first two letters as the profile picture
    private void displayUser(int index){
        String username = usernames.get(index);
        usernameTextView.setText(username);
        //Setting first two letters of the username for profile picture
        String initials = username.length() > 1 ? username.substring(0,2).toUpperCase() :  username.toUpperCase();
        profileTextView.setText(initials);

        int randomColor = randomColor();
        //Need to set the drawable background otherwise it will be a square just by setting the color
        Drawable background = getResources().getDrawable(R.drawable.circle_background);
        if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor(randomColor);
            profileTextView.setBackground(background);
        }
    }
    //Generates the random color for the profile picture
    private int randomColor(){
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

}