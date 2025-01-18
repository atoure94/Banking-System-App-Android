package com.example.banking_app_java;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput;
    Button loginButton;
    Button registerLink;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = findViewById(R.id.username_lab);
        passwordInput = findViewById(R.id.password_lab);
        loginButton = findViewById(R.id.login_btn);
        registerLink = findViewById(R.id.register_link);
        dbHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (checkLogin(username, password)) {
                    Intent intent = new Intent(MainActivity.this, ChoiceActivity.class);
                    intent.putExtra("username", username); // Transmettre le nom d'utilisateur
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InscriptionActivity.class);
            startActivity(intent);
        });

    }

    private boolean checkLogin(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.USERS_TABLE +
                " WHERE username = ? AND password = ?", new String[]{username, password});

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }
}
