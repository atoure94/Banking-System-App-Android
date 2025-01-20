package com.example.banking_app_java;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WithdrawActivity extends AppCompatActivity {

    EditText withdrawAmountInput;
    Button withdrawButton, backButton;
    DatabaseHelper dbHelper;
    String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        withdrawAmountInput = findViewById(R.id.withdraw_amount);
        withdrawButton = findViewById(R.id.withdraw_btn);
        backButton = findViewById(R.id.back_btn_withdraw);

        dbHelper = new DatabaseHelper(this);

        // Récupérer le nom d'utilisateur depuis l'Intent
        currentUsername = getIntent().getStringExtra("username");

        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = withdrawAmountInput.getText().toString();

                if (amountStr.isEmpty()) {
                    Toast.makeText(WithdrawActivity.this, "Please enter a valid amount.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double withdrawAmount = Double.parseDouble(amountStr);

                int userId = getUserId(currentUsername);
                if (userId != -1) {
                    if (withdrawFunds(userId, withdrawAmount)) {
                        Toast.makeText(WithdrawActivity.this, "Withdrawal successful!", Toast.LENGTH_SHORT).show();
                        withdrawAmountInput.setText("");
                    } else {
                        Toast.makeText(WithdrawActivity.this, "Insufficient balance.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WithdrawActivity.this, "Withdrawal failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(v -> {
            // Utiliser finish() pour revenir à l'écran précédent
            finish();
        });
    }

    private int getUserId(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + DatabaseHelper.USERS_TABLE + " WHERE username = ?",
                new String[]{username});

        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            cursor.close();
            return userId;
        }

        cursor.close();
        return -1;
    }

    private boolean withdrawFunds(int userId, double withdrawAmount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Vérifier si le solde est suffisant
        Cursor cursor = db.rawQuery("SELECT balance FROM " + DatabaseHelper.USERS_TABLE + " WHERE id = ?",
                new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            double currentBalance = cursor.getDouble(0);
            cursor.close();

            if (currentBalance >= withdrawAmount) {
                db.execSQL("UPDATE " + DatabaseHelper.USERS_TABLE + " SET balance = balance - ? WHERE id = ?",
                        new Object[]{withdrawAmount, userId});

                db.execSQL("INSERT INTO " + DatabaseHelper.TRANSACTIONS_TABLE + " (user_id, type, amount) VALUES (?, ?, ?)",
                        new Object[]{userId, "Withdraw", withdrawAmount});

                return true;
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return false;
    }
}
