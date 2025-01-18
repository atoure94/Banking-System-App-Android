package com.example.banking_app_java;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TransfertActivity extends AppCompatActivity {

    EditText recipientAccountInput, transferAmountInput;
    Button transferButton, backButton;
    DatabaseHelper dbHelper;
    String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfert);

        recipientAccountInput = findViewById(R.id.recipient_account);
        transferAmountInput = findViewById(R.id.transfer_amount);
        transferButton = findViewById(R.id.transfer_btn);
        backButton = findViewById(R.id.back_btn_transfert);

        dbHelper = new DatabaseHelper(this);


        currentUsername = getIntent().getStringExtra("username");

        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipientAccount = recipientAccountInput.getText().toString();
                String transferAmountStr = transferAmountInput.getText().toString();

                if (recipientAccount.isEmpty() || transferAmountStr.isEmpty()) {
                    Toast.makeText(TransfertActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double transferAmount = Double.parseDouble(transferAmountStr);

                if (transferFunds(recipientAccount, transferAmount)) {
                    Toast.makeText(TransfertActivity.this, "Transfer successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TransfertActivity.this, "Transfer failed. Insufficient funds or invalid account.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        backButton.setOnClickListener(v -> {

            finish();
        });
    }

    private boolean transferFunds(String recipientAccount, double transferAmount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        double currentBalance = getCurrentBalance();
        if (currentBalance < transferAmount) return false;

        // Vérifier l'existence du compte destinataire
        /*Cursor recipientCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.USERS_TABLE + " WHERE username = ?", new String[]{recipientAccount});
        if (!recipientCursor.moveToFirst()) {
            recipientCursor.close();
            return false;
        }
        recipientCursor.close();*/

        // Mettre à jour les soldes
        updateBalance(currentUsername, -transferAmount);
        updateBalance(recipientAccount, transferAmount);

        // Ajouter des transactions
        addTransaction(currentUsername, "Transfer", -transferAmount);
        addTransaction(recipientAccount, "Received", transferAmount);

        return true;
    }

    private double getCurrentBalance() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT balance FROM " + DatabaseHelper.USERS_TABLE + " WHERE username = ?",
                new String[]{currentUsername}
        );

        if (cursor.moveToFirst()) {
            double balance = cursor.getDouble(0);
            cursor.close();
            return balance;
        }

        cursor.close();
        return 0.0; // Retourne 0 si l'utilisateur n'existe pas (cas improbable ici)
    }

    private void updateBalance(String username, double amount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(
                "UPDATE " + DatabaseHelper.USERS_TABLE + " SET balance = balance + ? WHERE username = ?",
                new Object[]{amount, username}
        );
    }


    private void addTransaction(String username, String type, double amount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(
                "INSERT INTO " + DatabaseHelper.TRANSACTIONS_TABLE + " (username, type, amount, timestamp) VALUES (?, ?, ?, datetime('now'))",
                new Object[]{username, type, amount}
        );
    }

}