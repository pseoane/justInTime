package com.example.distributedproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private SeekBar seekBar;
    private TextView currentPriceLabel;
    private PahoClient client;
    private int currentSelectedPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        client = new PahoClient(getApplication());
        if (!client.getLightValue().hasObservers()) {
            client.getLightValue().observe(this, value -> {
                if (value <= currentSelectedPrice) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Current price is " + value + ". Turn the washing machine on!!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
        currentPriceLabel = findViewById(R.id.currentPriceTextView);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                currentPriceLabel.setText("Selected Price: " + progress + "€");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentSelectedPrice = seekBar.getProgress();
            }
        });
    }
}