package com.example.banking_app_java;

import static java.lang.System.exit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ChoiceActivity extends AppCompatActivity {

    Button balanceButton, depositButton, withdrawButton, transferButton, transactionsButton, exitButton;
    String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        balanceButton = findViewById(R.id.balance_choice_btn);
        depositButton = findViewById(R.id.deposit_choice_btn);
        withdrawButton = findViewById(R.id.withdraw_choice_btn);
        transferButton = findViewById(R.id.transfert_choice_btn);
        transactionsButton = findViewById(R.id.transactions_choice_btn);
        exitButton = findViewById(R.id.exit_btn);

        // Récupérer le nom d'utilisateur à partir de l'Intent
        currentUsername = getIntent().getStringExtra("username");

        balanceButton.setOnClickListener(v -> openActivity(BalanceActivity.class));
        depositButton.setOnClickListener(v -> openActivity(DepositActivity.class));
        withdrawButton.setOnClickListener(v -> openActivity(WithdrawActivity.class));
        transferButton.setOnClickListener(v -> openActivity(TransfertActivity.class));
        transactionsButton.setOnClickListener(v -> openActivity(TransactionActivity.class));

        exitButton.setOnClickListener(v -> {
            exit();
        });
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(ChoiceActivity.this, activityClass);
        intent.putExtra("username", currentUsername);
        startActivity(intent);
    }

    private void exit() {

        finish();


        System.exit(0);
    }
}
