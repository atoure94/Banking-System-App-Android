package com.example.banking_app_java;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.banking_app_java.DatabaseHelper;
import com.example.banking_app_java.R;

public class WithdrawActivity extends AppCompatActivity {

    EditText withdrawAmountInput;
    Button withdrawButton,  backButton;
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

        backButton.setOnClickListener(v -> {
            // Utiliser finish() pour revenir à l'écran précédent
            finish();
        });

        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String withdrawAmountStr = withdrawAmountInput.getText().toString().trim();

                if (withdrawAmountStr.isEmpty()) {
                    Toast.makeText(WithdrawActivity.this, "Enter a valid amount.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double withdrawAmount;
                try {
                    withdrawAmount = Double.parseDouble(withdrawAmountStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(WithdrawActivity.this, "Invalid amount entered.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (withdrawAmount <= 0) {
                    Toast.makeText(WithdrawActivity.this, "Amount must be greater than zero.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (withdrawFunds(withdrawAmount)) {
                    Toast.makeText(WithdrawActivity.this, "Withdrawal successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WithdrawActivity.this, "Insufficient funds.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean withdrawFunds(double withdrawAmount) {

        if (currentUsername == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return false;
        }

        double currentBalance = getCurrentBalance(currentUsername);

        // Vérification du solde
        if (currentBalance < withdrawAmount) {
            return false;
        }

        // Mise à jour du solde
        updateBalance(currentUsername, -withdrawAmount);

        // Enregistrement de la transaction
        addTransaction(currentUsername, "Withdrawal", -withdrawAmount);

        return true;
    }

    private double getCurrentBalance(String username) {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT balance FROM " + DatabaseHelper.USERS_TABLE + " WHERE username = ?",
                new String[]{username}
        );

        if (cursor.moveToFirst()) {
            double balance = cursor.getDouble(0);
            cursor.close();
            return balance;
        }

        cursor.close();
        return 0.0;
    }

    private void updateBalance(String username, double amount) {
        dbHelper.getWritableDatabase().execSQL(
                "UPDATE " + DatabaseHelper.USERS_TABLE + " SET balance = balance + ? WHERE username = ?",
                new Object[]{amount, username}
        );
    }

    private void addTransaction(String username, String type, double amount) {
        dbHelper.getWritableDatabase().execSQL(
                "INSERT INTO " + DatabaseHelper.TRANSACTIONS_TABLE + " (username, type, amount, timestamp) VALUES (?, ?, ?, datetime('now'))",
                new Object[]{username, type, amount}
        );
    }
}
