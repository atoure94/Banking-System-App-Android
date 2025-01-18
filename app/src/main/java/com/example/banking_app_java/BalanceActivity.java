package com.example.banking_app_java;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BalanceActivity extends AppCompatActivity {

    TextView balanceTextView;
    DatabaseHelper dbHelper;
    String currentUsername;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        balanceTextView = findViewById(R.id.balance_textview);
        dbHelper = new DatabaseHelper(this);
        backButton = findViewById(R.id.back_btn_balance);

        // Récupérer le nom d'utilisateur depuis l'Intent
        currentUsername = getIntent().getStringExtra("username");

        displayBalance();


        backButton.setOnClickListener(v -> {
            // Utiliser finish() pour revenir à l'écran précédent
            finish();
        });
    }

    private void displayBalance() {
        if (currentUsername == null) {
            balanceTextView.setText("Balance not available.");
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT balance FROM " + DatabaseHelper.USERS_TABLE + " WHERE username = ?",
                new String[]{currentUsername});

        if (cursor.moveToFirst()) {
            double balance = cursor.getDouble(0);
            balanceTextView.setText("Your Balance: " + balance + " $");
        } else {
            balanceTextView.setText("Balance not available.");
        }

        cursor.close();
    }
}
