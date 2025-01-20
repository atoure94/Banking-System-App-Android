package com.example.banking_app_java;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TransfertActivity extends AppCompatActivity {

    // Déclaration des widgets et variables
    EditText recipientAccountInput, transferAmountInput;
    Button transferButton, backButton;
    DatabaseHelper dbHelper;
    String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfert);

        // Initialisation des widgets
        recipientAccountInput = findViewById(R.id.recipient_account);
        transferAmountInput = findViewById(R.id.transfer_amount);
        transferButton = findViewById(R.id.transfer_btn);
        backButton = findViewById(R.id.back_btn_transfert);

        dbHelper = new DatabaseHelper(this);

        // Récupération du nom d'utilisateur actuel depuis l'Intent
        currentUsername = getIntent().getStringExtra("username");

        // Gestion du clic sur le bouton "Transfer"
        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipientAccount = recipientAccountInput.getText().toString();
                String transferAmountStr = transferAmountInput.getText().toString();

                if (recipientAccount.isEmpty() || transferAmountStr.isEmpty()) {
                    Toast.makeText(TransfertActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double transferAmount = Double.parseDouble(transferAmountStr);

                    int userId = getUserId(currentUsername);

                    // Vérifier et effectuer le transfert

                    if(userId != -1){
                        if (transfertFunds(userId, transferAmount)) {
                            Toast.makeText(TransfertActivity.this, "Transfer successful!", Toast.LENGTH_SHORT).show();

                            recipientAccountInput.setText("");
                            transferAmountInput.setText("");
                        } else {
                            Toast.makeText(TransfertActivity.this, "Transfer failed. Insufficient funds.", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(TransfertActivity.this, "Invalid amount format.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Gestion du clic sur le bouton "Retour"
        backButton.setOnClickListener(v -> finish());
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



    private boolean transfertFunds(int userId, double transfertAmount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Vérifier si le solde est suffisant
        Cursor cursor = db.rawQuery("SELECT balance FROM " + DatabaseHelper.USERS_TABLE + " WHERE id = ?",
                new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            double currentBalance = cursor.getDouble(0);
            cursor.close();

            if (currentBalance >= transfertAmount) {
                db.execSQL("UPDATE " + DatabaseHelper.USERS_TABLE + " SET balance = balance - ? WHERE id = ?",
                        new Object[]{transfertAmount, userId});

                db.execSQL("INSERT INTO " + DatabaseHelper.TRANSACTIONS_TABLE + " (user_id, type, amount) VALUES (?, ?, ?)",
                        new Object[]{userId, "Transfert", transfertAmount});

                return true;
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return false;
    }
}
