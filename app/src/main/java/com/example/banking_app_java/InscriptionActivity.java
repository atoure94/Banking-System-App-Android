package com.example.banking_app_java;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class InscriptionActivity extends AppCompatActivity {

    EditText accountNumberInput, usernameInput, passwordInput, confirmPasswordInput;
    CheckBox agreeCheckBox;
    Button registerButton, backButton;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        accountNumberInput = findViewById(R.id.count_number);
        usernameInput = findViewById(R.id.username_lab2);
        passwordInput = findViewById(R.id.password_lab2);
        confirmPasswordInput = findViewById(R.id.confirmPassword_lab2);
        agreeCheckBox = findViewById(R.id.agree_checkBox);
        registerButton = findViewById(R.id.register_btn);
        backButton = findViewById(R.id.back_btn);

        dbHelper = new DatabaseHelper(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountNumber = accountNumberInput.getText().toString();
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();

                if (validateInputs(accountNumber, username, password, confirmPassword)) {
                    if (registerUser(accountNumber, username, password)) {
                        Toast.makeText(InscriptionActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        finish(); // Retour à l'activité précédente
                    } else {
                        Toast.makeText(InscriptionActivity.this, "Username already exists.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        backButton.setOnClickListener(v -> {
            // Utiliser finish() pour revenir à l'écran précédent
            finish();
        });
    }

    private boolean validateInputs(String accountNumber, String username, String password, String confirmPassword) {
        if (accountNumber.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!agreeCheckBox.isChecked()) {
            Toast.makeText(this, "You must agree to the terms and conditions.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean registerUser(String accountNumber, String username, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Vérifier si le nom d'utilisateur existe déjà
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.USERS_TABLE + " WHERE username = ?", new String[]{username});
        if (cursor.getCount() > 0) {
            cursor.close();
            return false;
        }
        cursor.close();

        // Ajouter un nouvel utilisateur
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("balance", 0); // Solde initial par défaut
        db.insert(DatabaseHelper.USERS_TABLE, null, values);
        return true;
    }
}
