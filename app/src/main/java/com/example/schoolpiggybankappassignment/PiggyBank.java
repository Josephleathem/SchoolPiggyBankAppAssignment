package com.example.schoolpiggybankappassignment;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class PiggyBank extends AppCompatActivity {
    private static final double QUARTERS = 0.25;
    private static final double DIMES = 0.10;
    private static final double NICKELS = 0.05;
    private static final double PENNIES = 0.01;

    private ToneGenerator toneGenerator;

    private EditText numQuarters;
    private EditText numDimes;
    private EditText numNickels;
    private EditText numPennies;

    private TextView quartersVisual;
    private TextView dimesVisual;
    private TextView nickelsVisual;
    private TextView penniesVisual;

    private TextView result;
    private Spinner group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piggy_bank);

        toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 80);

        numQuarters = findViewById(R.id.etQuartersCount);
        numDimes = findViewById(R.id.etDimesCount);
        numNickels = findViewById(R.id.etNickelsCount);
        numPennies = findViewById(R.id.etPenniesCount);

        quartersVisual = findViewById(R.id.tvQuartersVisual);
        dimesVisual = findViewById(R.id.tvDimesVisual);
        nickelsVisual = findViewById(R.id.tvNickelsVisual);
        penniesVisual = findViewById(R.id.tvPenniesVisual);

        group = findViewById(R.id.spGroup);
        result = findViewById(R.id.tvResult);

        Button minusQuarters = findViewById(R.id.btMinusQuarters);
        Button addQuarters = findViewById(R.id.btAddQuarters);
        Button minusDimes = findViewById(R.id.btMinusDimes);
        Button addDimes = findViewById(R.id.btAddDimes);
        Button minusNickels = findViewById(R.id.btMinusNickels);
        Button addNickels = findViewById(R.id.btAddNickels);
        Button minusPennies = findViewById(R.id.btMinusPennies);
        Button addPennies = findViewById(R.id.btAddPennies);
        Button calc = findViewById(R.id.btCalc);
        Button reset = findViewById(R.id.btReset);

        minusQuarters.setOnClickListener(v -> updateCount(numQuarters, quartersVisual, -1));
        addQuarters.setOnClickListener(v -> updateCount(numQuarters, quartersVisual, 1));

        minusDimes.setOnClickListener(v -> updateCount(numDimes, dimesVisual, -1));
        addDimes.setOnClickListener(v -> updateCount(numDimes, dimesVisual, 1));

        minusNickels.setOnClickListener(v -> updateCount(numNickels, nickelsVisual, -1));
        addNickels.setOnClickListener(v -> updateCount(numNickels, nickelsVisual, 1));

        minusPennies.setOnClickListener(v -> updateCount(numPennies, penniesVisual, -1));
        addPennies.setOnClickListener(v -> updateCount(numPennies, penniesVisual, 1));

        calc.setOnClickListener(v -> calculateTotal());
        reset.setOnClickListener(v -> resetPiggyBank());

        attachCountInputWatcher(numQuarters, quartersVisual);
        attachCountInputWatcher(numDimes, dimesVisual);
        attachCountInputWatcher(numNickels, nickelsVisual);
        attachCountInputWatcher(numPennies, penniesVisual);

        updateVisual(quartersVisual, getCount(numQuarters));
        updateVisual(dimesVisual, getCount(numDimes));
        updateVisual(nickelsVisual, getCount(numNickels));
        updateVisual(penniesVisual, getCount(numPennies));
    }

    private void attachCountInputWatcher(EditText countField, TextView visualField) {
        countField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No-op.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No-op.
            }

            @Override
            public void afterTextChanged(Editable s) {
                int current = getCount(countField);

                // Normalize invalid/negative input without retrigger loops.
                String normalized = String.valueOf(current);
                if (!normalized.equals(s.toString())) {
                    countField.removeTextChangedListener(this);
                    countField.setText(normalized);
                    countField.setSelection(normalized.length());
                    countField.addTextChangedListener(this);
                }

                updateVisual(visualField, current);
            }
        });
    }

    private void updateCount(EditText countField, TextView visualField, int delta) {
        int current = getCount(countField);
        int updated = Math.max(0, current + delta);

        if (updated == current) {
            return;
        }

        setCount(countField, updated);
        updateVisual(visualField, updated);
        playFeedback(visualField);
    }

    private int getCount(EditText countField) {
        String text = countField.getText().toString().trim();
        if (text.isEmpty()) {
            return 0;
        }

        try {
            int parsed = Integer.parseInt(text);
            return Math.max(0, parsed);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setCount(EditText countField, int value) {
        countField.setText(String.valueOf(value));
    }

    private void updateVisual(TextView visualField, int count) {
        if (count == 0) {
            visualField.setText(getString(R.string.noCoins));
            return;
        }

        int shown = Math.min(count, 12);
        StringBuilder icons = new StringBuilder();
        for (int i = 0; i < shown; i++) {
            icons.append("o ");
        }

        if (count > shown) {
            visualField.setText(getString(R.string.visualCoinTextWithMore, count, icons.toString().trim(), count - shown));
        } else {
            visualField.setText(getString(R.string.visualCoinText, count, icons.toString().trim()));
        }
    }

    private void playFeedback(TextView visualField) {
        if (toneGenerator != null) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 80);
            visualField.postDelayed(() -> toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100), 90);
        }

        visualField.setAlpha(0.6f);
        visualField.setTranslationY(-14f);
        visualField.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(180)
                .start();
    }

    private void calculateTotal() {
        String groupChoice = group.getSelectedItem().toString();

        int qCount = getCount(numQuarters);
        int dCount = getCount(numDimes);
        int nCount = getCount(numNickels);
        int pCount = getCount(numPennies);

        double total = qCount * QUARTERS + dCount * DIMES + nCount * NICKELS + pCount * PENNIES;
        DecimalFormat currency = new DecimalFormat("$0.00");

        if ("Save".equalsIgnoreCase(groupChoice)) {
            result.setText(getString(R.string.saveResult, currency.format(total)));
        } else if ("Spend".equalsIgnoreCase(groupChoice)) {
            result.setText(getString(R.string.spendResult, currency.format(total)));
        } else {
            result.setText(getString(R.string.pickSaveSpend));
        }
    }

    private void resetPiggyBank() {
        setCount(numQuarters, 0);
        setCount(numDimes, 0);
        setCount(numNickels, 0);
        setCount(numPennies, 0);

        updateVisual(quartersVisual, 0);
        updateVisual(dimesVisual, 0);
        updateVisual(nickelsVisual, 0);
        updateVisual(penniesVisual, 0);

        result.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toneGenerator != null) {
            toneGenerator.release();
            toneGenerator = null;
        }

    }
}