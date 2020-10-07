package com.lenecoproekt.minesweeper;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.*;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{
    private SeekBar heightBar;
    private SeekBar widthBar;
    private SeekBar minesBar;
    private TextView height;
    private TextView width;
    private TextView mines;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = findViewById(R.id.imageView);
        heightBar = findViewById(R.id.heightChooser);
        widthBar = findViewById(R.id.widthChooser);
        minesBar = findViewById(R.id.minesProcent);
        height = findViewById(R.id.heigthNumber);
        width = findViewById(R.id.widthNumber);
        mines = findViewById(R.id.minesProcentNumber);
        heightBar.setProgress(10);
        heightBar.setMax(12);
        height.setText(String.valueOf(heightBar.getProgress()));
        widthBar.setProgress(10);
        widthBar.setMax(12);
        width.setText(String.valueOf(widthBar.getProgress()));
        minesBar.setProgress(10);
        minesBar.setMax(30);
        mines.setText(String.valueOf(minesBar.getProgress()));
        heightBar.setOnSeekBarChangeListener(this);
        widthBar.setOnSeekBarChangeListener(this);
        minesBar.setOnSeekBarChangeListener(this);
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("HEIGHT", heightBar.getProgress()).putExtra("WIDTH", widthBar.getProgress()).putExtra("MINES", minesBar.getProgress());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == heightBar) {
            height.setText(String.valueOf(seekBar.getProgress()));
        } else if (seekBar == widthBar){
            width.setText(String.valueOf(seekBar.getProgress()));
        } else if (seekBar == minesBar) {
            mines.setText(String.valueOf(seekBar.getProgress()));
        }
    }
}