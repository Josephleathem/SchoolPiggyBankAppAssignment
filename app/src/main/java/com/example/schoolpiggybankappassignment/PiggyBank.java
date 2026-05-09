package com.example.schoolpiggybankappassignment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class PiggyBank extends AppCompatActivity {
    double Quarters = 0.25;
    double Dimes = 0.10;
    double Nickels = 0.05;
    double Pennies = 0.01;
    double Tquarters;
    double Tdimes;
    double Tnickels;
    double Tpennies;
    double total;
    String groupChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piggy_bank);
        final EditText numQuarters = findViewById(R.id.tvQuarters);
        final EditText numDimes = findViewById(R.id.tvDimes);
        final EditText numNickels = findViewById(R.id.tvNickels);
        final EditText numPennies = findViewById(R.id.tvPennies);
        final Spinner group = (Spinner)findViewById(R.id.spGroup);
        Button Calc = (Button)findViewById(R.id.btCalc);
        Calc.setOnClickListener(new View.OnClickListener() {
            final TextView result = findViewById(R.id.tvResult);
            @Override
            public void onClick(View v) {
                groupChoice = group.getSelectedItem().toString();

                String qText = numQuarters.getText().toString().trim();
                String dText = numDimes.getText().toString().trim();
                String nText = numNickels.getText().toString().trim();
                String pText = numPennies.getText().toString().trim();

                int qCount;
                int dCount;
                int nCount;
                int pCount;

                try {
                    qCount = qText.isEmpty() ? 0 : Integer.parseInt(qText);
                    dCount = dText.isEmpty() ? 0 : Integer.parseInt(dText);
                    nCount = nText.isEmpty() ? 0 : Integer.parseInt(nText);
                    pCount = pText.isEmpty() ? 0 : Integer.parseInt(pText);
                } catch (NumberFormatException e) {
                    result.setText("Please enter whole numbers only.");
                    return;
                }

                Tquarters = qCount;
                Tdimes = dCount;
                Tnickels = nCount;
                Tpennies = pCount;
                total = Tquarters * Quarters + Tdimes * Dimes + Tnickels * Nickels + Tpennies * Pennies;
                DecimalFormat currency = new DecimalFormat("$###,###.##");
                if ("Deposit".equalsIgnoreCase(groupChoice)) {
                    result.setText("You would like to " + groupChoice + " " + currency.format(total) + " to your piggy bank.");
                } else if ("Withdraw".equalsIgnoreCase(groupChoice)) {
                    result.setText("You would like to " + groupChoice + " " + currency.format(total) + " from your piggy bank.");
                } else {
                    result.setText("Please choose Deposit or Withdraw.");
                }
            }
            });

    }
}