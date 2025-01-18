package com.example.banking_app_java;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TransactionActivity extends AppCompatActivity {

    ListView transactionListView;
    DatabaseHelper dbHelper;
    String currentUsername;
    Button  backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        transactionListView = findViewById(R.id.transaction_listview);
        dbHelper = new DatabaseHelper(this);
        backButton = findViewById(R.id.back_btn_transactions);

        // Récupérer le username depuis l'Intent
        currentUsername = getIntent().getStringExtra("username");

        displayTransactions();

        backButton.setOnClickListener(v -> {
            // Utiliser finish() pour revenir à l'écran précédent
            finish();
        });
    }

    private void displayTransactions() {
        if (currentUsername == null) return;

        // Récupérer l'ID de l'utilisateur
        int userId = getUserId(currentUsername);
        if (userId == -1) {
            return; // Aucun utilisateur trouvé, donc aucune transaction à afficher
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT type, amount, timestamp FROM " + DatabaseHelper.TRANSACTIONS_TABLE + " WHERE user_id = ?",
                new String[]{String.valueOf(userId)});

        ArrayList<String> transactions = new ArrayList<>();

        while (cursor.moveToNext()) {
            String type = cursor.getString(0);
            double amount = cursor.getDouble(1);
            String timestamp = cursor.getString(2);
            transactions.add(type + ": " + amount + " $ (" + timestamp + ")");
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, transactions);
        transactionListView.setAdapter(adapter);
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
        return -1; // Utilisateur introuvable
    }

}
