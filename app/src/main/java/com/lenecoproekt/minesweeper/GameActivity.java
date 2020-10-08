package com.lenecoproekt.minesweeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    int height;
    int width;
    private GameObject [][] gameObjects;
    private int minesNumber;
    private int flagCounter;
    private int closedTiles;
    private int countMines = 0;
    private Button[][] buttons;
    private boolean isGameStopped;
    private TextView flags;
    private TextView score;
    private int scoreN = 0;
    private Chronometer mChronometer;
    private boolean win;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        height = getIntent().getExtras().getInt("HEIGHT");
        width = getIntent().getExtras().getInt("WIDTH");
        minesNumber = height*width * getIntent().getExtras().getInt("MINES") / 100;
        closedTiles = height*width;
        gameObjects = new GameObject[height][width];
        TableLayout gameField = findViewById(R.id.gameField);
        gameField.setStretchAllColumns(true);
        gameField.setShrinkAllColumns(true);
        TextView mines = findViewById(R.id.minesNumber);
        TextView minesIco = findViewById(R.id.mineIco);
        minesIco.setText("\uD83D\uDCA9");
        flags = findViewById(R.id.flagsNumber);
        TextView flagsIco = findViewById(R.id.flagIco);
        flagsIco.setText("\uD83D\uDC31");
        score = findViewById(R.id.score);
        TextView scoreIco = findViewById(R.id.scoreIco);
        scoreIco.setText("\uD83D\uDC53");
        score.setText(String.valueOf(0));
        TextView timeIco = findViewById(R.id.timeIco);
        timeIco.setText("\uD83D\uDD57");
        TextView field = findViewById(R.id.textView);
        field.setText(String.valueOf(height) + " X " + String.valueOf(width));
        buttons = new Button[height][width];
        loadMines();
        loadNumbers();
        mines.setText(String.valueOf(countMines));
        flagCounter = countMines;
        flags.setText(String.valueOf(flagCounter));
        Button restart = findViewById(R.id.restart);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mChronometer = findViewById(R.id.chronometer2);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();

        for (int i = 0; i < height; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    1));
            for (int j = 0; j < width; j++) {
                final Button button = new Button(this);
                button.setTextSize(24 / ((float)(width)/10));
                button.setBackground(ContextCompat.getDrawable(this, R.drawable.b2a));
                buttons[i][j] = button;
                tableRow.addView(button, j);
                final int finalI = i;
                final int finalJ = j;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isGameStopped) {
                            openTile(button, finalI, finalJ);
                        }
                    }
                });
                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        flagOn(button, finalI, finalJ);
                        return true;
                        }
                });
            }
            gameField.addView(tableRow, i);
        }
    }

    private void flagOn(Button button, int i, int j){
        if (!isGameStopped) {
            if (!gameObjects[i][j].isOpen) {
                if (!gameObjects[i][j].isFlag) {
                    if (flagCounter > 0) {
                        button.setText("\uD83D\uDC31");
                        gameObjects[i][j].isFlag = true;
                        flagCounter--;
                        flags.setText(String.valueOf(flagCounter));
                    }
                } else {
                    button.setText("");
                    flagCounter++;
                    flags.setText(String.valueOf(flagCounter));
                }
            }
        }
    }

    private void openTile(final Button button, int finalI, int finalJ){
        if (gameObjects[finalI][finalJ].isMine) {
            button.setText("\uD83D\uDCA9");
            gameOver();
        }
        else {
            closedTiles--;
            scoreN +=5;
            score.setText(String.valueOf(scoreN));
            if (gameObjects[finalI][finalJ].countMineNeighbors == 0) {
                button.setText("\uD83D\uDC3E");
                gameObjects[finalI][finalJ].isOpen = true;
                List<GameObject> neighbors = getNeighbors(gameObjects[finalI][finalJ]);
                for (int i = 0; i < neighbors.size(); i++) {
                    if (!neighbors.get(i).isOpen) {
                        gameObjects[finalI][finalJ].isOpen = true;
                        final int indexI = neighbors.get(i).x;
                        final int indexJ = neighbors.get((i)).y;
                        openTile(buttons[indexI][indexJ], indexI, indexJ);
                    }

                }
            } else {
                gameObjects[finalI][finalJ].isOpen = true;
                button.setText(String.valueOf(gameObjects[finalI][finalJ].countMineNeighbors));
            }
            if (closedTiles == countMines) win();
        }
    }

    private void win() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (gameObjects[i][j].isMine) buttons[i][j].setText("\uD83D\uDC31");
            }
        }
        isGameStopped = true;
        mChronometer.stop();
        ImageView smile = findViewById(R.id.smile);
        smile.setImageResource(R.drawable.smiling_cat);
        win = true;
        Button saveRecord = findViewById(R.id.saveRecord);
        saveRecord.setVisibility(View.VISIBLE);
        saveRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // отрываем поток для записи
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                            openFileOutput("records.txt", MODE_PRIVATE)));
                    // пишем данные
                    bw.write("Содержимое файла");
                    // закрываем поток
                    bw.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(GameActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(GameActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Toast.makeText(this, R.string.success, Toast.LENGTH_LONG).show();
    }

    private void gameOver() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (gameObjects[i][j].isMine) buttons[i][j].setText("\uD83D\uDCA9");
                else {
                    if (gameObjects[i][j].countMineNeighbors == 0) buttons[i][j].setText("\uD83D\uDC3E");
                    else buttons[i][j].setText(String.valueOf(gameObjects[i][j].countMineNeighbors));
                }
                gameObjects[i][j].isOpen = true;
            }
        }
        isGameStopped = true;
        mChronometer.stop();
        ImageView smile = findViewById(R.id.smile);
        smile.setImageResource(R.drawable.crying_cat_face);
        Toast.makeText(this, R.string.gameOver, Toast.LENGTH_LONG).show();
    }

    private void loadNumbers() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!gameObjects[i][j].isMine){
                    int n = 0;
                    List<GameObject> result = getNeighbors(gameObjects[i][j]);
                    for (int k = 0; k < result.size(); k++) {
                        if (result.get(k).isMine) n++;
                    }
                    gameObjects[i][j].countMineNeighbors = n;
                }
            }
        }
    }

    private void loadMines() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (gameObjects[i][j] == null) {
                    boolean isMine;
                    if (countMines == minesNumber) {
                        isMine = false;
                    }
                    else {
                        isMine = (int) (Math.random() * ((width * height) / minesNumber)) < 1;
                        if (isMine) countMines++;
                    }
                    gameObjects[i][j] = new GameObject(i, j, isMine);
                }
                else {
                    if (countMines < minesNumber){
                        if (!gameObjects[i][j].isMine){
                           gameObjects[i][j].isMine = Math.random() * 10 < 1;
                            if (gameObjects[i][j].isMine) countMines++;
                        }
                    }
                    else return;
                }
            }
        }
        if (countMines < minesNumber) loadMines();
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= width) {
                    continue;
                }
                if (x < 0 || x >= height) {
                    continue;
                }
                if (gameObjects[x][y] == gameObject) {
                    continue;
                }
                result.add(gameObjects[x][y]);
            }
        }
        return result;
    }
}