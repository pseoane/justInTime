package com.example.distributedproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private SeekBar seekBar;
    private TextView selectedPriceLabel;
    private TextView currentPriceLabel;
    private TextView washingMachineStatusLabel;
    private Switch autoOnSwitch;
    private PahoClient client;
    private Float currentSelectedPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        autoOnSwitch = findViewById(R.id.turnMachineOnSwitch);
        washingMachineStatusLabel = findViewById(R.id.washingMachineStatusLabel);
        seekBar = findViewById(R.id.seekBar);
        autoOnSwitch.setOnClickListener(view -> switchButtonClicked());
        currentPriceLabel = findViewById(R.id.selectedPriceLabel);
        currentSelectedPrice = 0f;
        client = new PahoClient(getApplication());
        if (!client.getLightValue().hasObservers()) {
            client.getLightValue().observe(this, value -> {
                if (value <= currentSelectedPrice && autoOnSwitch.isChecked()) {
                    // Turn washing machine on
                    turnMachineOn();
                    Toast toast = Toast.makeText(getApplicationContext(), "Current price is " + value + ". Turn the washing machine on!!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                currentPriceLabel.setText("Current light price is: " + value + "€");
            });
        }

        if (!client.getWashingMachineStatus().hasObservers()) {
            client.getWashingMachineStatus().observe(this, value -> {
                // Set status
                if (value.equals("ON")) {
                    washingMachineStatusLabel.setText("Washing machine is ON");
                } else {
                    washingMachineStatusLabel.setText("Washing machine is OFF");
                }
            });
        }

        selectedPriceLabel = findViewById(R.id.currentPriceTextView);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                selectedPriceLabel.setText("Selected Price: " + progress + "€");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentSelectedPrice = new Integer(seekBar.getProgress()).floatValue();
                Float currentLightValue = client.getLightValue().getValue();
                if (currentSelectedPrice >= currentLightValue && autoOnSwitch.isChecked()) {
                    turnMachineOn();
                }
            }
        });
    }

    private void turnMachineOn() {
        washingMachineStatusLabel.setText("Waiting for washing machine to turn on");
        autoOnSwitch.setChecked(false);
        client.publishTurnOnOrder();
    }

    private void switchButtonClicked() {
        Float currentLightValue = client.getLightValue().getValue();
        if (currentSelectedPrice >= currentLightValue && autoOnSwitch.isChecked()) {
            turnMachineOn();
        }
    }
}