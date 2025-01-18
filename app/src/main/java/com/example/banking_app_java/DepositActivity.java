package com.example.banking_app_java;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DepositActivity extends AppCompatActivity {

    EditText depositCodeInput;
    Button depositButton,backButton ;
    DatabaseHelper dbHelper;
    String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        depositCodeInput = findViewById(R.id.deposit_code);
        depositButton = findViewById(R.id.deposit_btn);
        backButton = findViewById(R.id.back_btn_deposit);

        dbHelper = new DatabaseHelper(this);

        // Récupérer le nom d'utilisateur depuis l'Intent
        currentUsername = getIntent().getStringExtra("username");

        depositButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String depositCode = depositCodeInput.getText().toString();

                if (depositCode.length() != 14) {
                    Toast.makeText(DepositActivity.this, "Code must be 14 characters long.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String amountStr = depositCode.substring(10);
                double depositAmount = Double.parseDouble(amountStr);

                int userId = getUserId(currentUsername);
                if (userId != -1 && depositFunds(userId, depositAmount)) {
                    Toast.makeText(DepositActivity.this, "Deposit successful!", Toast.LENGTH_SHORT).show();
                    depositCodeInput.setText("");
                } else {
                    Toast.makeText(DepositActivity.this, "Deposit failed.", Toast.LENGTH_SHORT).show();
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

    private boolean depositFunds(int userId, double depositAmount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE " + DatabaseHelper.USERS_TABLE + " SET balance = balance + ? WHERE id = ?",
                new Object[]{depositAmount, userId});

        db.execSQL("INSERT INTO " + DatabaseHelper.TRANSACTIONS_TABLE + " (user_id, type, amount) VALUES (?, ?, ?)",
                new Object[]{userId, "Deposit", depositAmount});

        return true;
    }
}
