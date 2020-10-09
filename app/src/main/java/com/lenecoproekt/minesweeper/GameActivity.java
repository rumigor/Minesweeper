package com.lenecoproekt.minesweeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
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

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.YELLOW;

public class GameActivity extends AppCompatActivity {
    int height;
    int width;
    private GameObject [][] gameObjects;
    private int minesNumber;
    private int flagCounter;
    private int closedTiles;
    private int countMines = 0;
    private TextView[][] cells;
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
        cells = new TextView[height][width];
        loadMines();
        loadNumbers();
        mines.setText(String.valueOf(countMines));
        flagCounter = countMines;
        flags.setText(String.valueOf(flagCounter));
        Button restart = findViewById(R.id.restart);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
        mChronometer = findViewById(R.id.chronometer2);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int widthD = size.x;
        int heightD = size.y;
        for (int i = 0; i < height; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    1));
            for (int j = 0; j < width; j++) {
                final TextView cell = new TextView(this);
                cell.setMaxHeight((int)(heightD/height*0.8));
                cell.setTextSize(24 / ((float)(width+height)/20));
                cell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                setBackgroundForCells(cell, R.drawable.blue50, R.drawable.blue40, R.drawable.blue30, R.drawable.blue20, R.drawable.blue15, R.drawable.blue10);
                cells[i][j] = cell;
                tableRow.addView(cell, j);
                final int finalI = i;
                final int finalJ = j;
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isGameStopped) {
                            openTile(cell, finalI, finalJ);
                        }
                    }
                });
                cell.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        flagOn(cell, finalI, finalJ);
                        return true;
                        }
                });
            }
            gameField.addView(tableRow, i);
        }
    }

    private void flagOn(TextView cell, int i, int j){
        if (!isGameStopped) {
            if (!gameObjects[i][j].isOpen) {
                if (!gameObjects[i][j].isFlag) {
                    if (flagCounter > 0) {
                        setBackgroundForCells(cell, R.drawable.green50, R.drawable.green40, R.drawable.green30, R.drawable.green20, R.drawable.green15, R.drawable.green10);
                        cell.setText("\uD83D\uDC31");
                        gameObjects[i][j].isFlag = true;
                        flagCounter--;
                        flags.setText(String.valueOf(flagCounter));
                    }
                } else {
                    setBackgroundForCells(cell, R.drawable.blue50, R.drawable.blue40, R.drawable.blue30, R.drawable.blue20, R.drawable.blue15, R.drawable.blue10);
                    cell.setText("");
                    flagCounter++;
                    flags.setText(String.valueOf(flagCounter));
                }
            }
        }
    }

    private void openTile(final TextView cell, int finalI, int finalJ){
        if (gameObjects[finalI][finalJ].isMine) {
            setBackgroundForCells(cell, R.drawable.red50, R.drawable.bomb40, R.drawable.red30, R.drawable.bomb20, R.drawable.red15, R.drawable.bomb10);
            cell.setText("\uD83D\uDCA9");
            gameOver();
        }
        else {
            closedTiles--;
            scoreN +=5;
            score.setText(String.valueOf(scoreN));
            if (gameObjects[finalI][finalJ].isFlag){
                gameObjects[finalI][finalJ].isFlag = false;
                flagCounter++;
                flags.setText(String.valueOf(flagCounter));
            }
            if (gameObjects[finalI][finalJ].countMineNeighbors == 0) {
                setBackgroundForCells(cell, R.drawable.open50, R.drawable.open40, R.drawable.open30, R.drawable.open20, R.drawable.open15,R.drawable.open10);
                cell.setText("\uD83D\uDC3E");
                gameObjects[finalI][finalJ].isOpen = true;
                List<GameObject> neighbors = getNeighbors(gameObjects[finalI][finalJ]);
                for (int i = 0; i < neighbors.size(); i++) {
                    if (!neighbors.get(i).isOpen) {
                        gameObjects[finalI][finalJ].isOpen = true;
                        final int indexI = neighbors.get(i).x;
                        final int indexJ = neighbors.get((i)).y;
                        openTile(cells[indexI][indexJ], indexI, indexJ);
                    }

                }
            } else {
                gameObjects[finalI][finalJ].isOpen = true;
                setBackgroundForCells(cell, R.drawable.open50, R.drawable.open40, R.drawable.open30, R.drawable.open20, R.drawable.open15,R.drawable.open10);
                cell.setText(String.valueOf(gameObjects[finalI][finalJ].countMineNeighbors));
                setNumberColor(cell, gameObjects[finalI][finalJ].countMineNeighbors);
            }
            if (closedTiles == countMines) win();
        }
    }

    private void win() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                setBackgroundForCells(cells[i][j], R.drawable.open50, R.drawable.open40, R.drawable.open30, R.drawable.open20, R.drawable.open15, R.drawable.open10);
                if (gameObjects[i][j].isMine) cells[i][j].setText("\uD83D\uDC31");
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
                setBackgroundForCells(cells[i][j], R.drawable.open50, R.drawable.open40, R.drawable.open30, R.drawable.open20, R.drawable.open15, R.drawable.open10);
                if (gameObjects[i][j].isMine) {
                    cells[i][j].setText("\uD83D\uDCA9");
                    setBackgroundForCells(cells[i][j], R.drawable.red50, R.drawable.bomb40, R.drawable.red30, R.drawable.bomb20, R.drawable.red15,R.drawable.bomb10);
                }
                else {
                    if (gameObjects[i][j].countMineNeighbors == 0) cells[i][j].setText("\uD83D\uDC3E");
                    else cells[i][j].setText(String.valueOf(gameObjects[i][j].countMineNeighbors));
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



    private void setBackgroundForCells(TextView cell, int size1, int size2, int size3, int size4, int size5, int size6) {
            if (height <= 11) {
                cell.setBackground(ContextCompat.getDrawable(this, size1));
            } else if (height < 15) {
                cell.setBackground(ContextCompat.getDrawable(this, size2));
            } else if (height < 20) {
                cell.setBackground(ContextCompat.getDrawable(this, size3));
            } else if (height < 30) {
                cell.setBackground(ContextCompat.getDrawable(this, size4));
            } else if (height < 40) {
                cell.setBackground(ContextCompat.getDrawable(this, size5));
            } else cell.setBackground(ContextCompat.getDrawable(this, size6));
    }

    private void setNumberColor(TextView cell, int number){
        switch (number){
            case 1:
                cell.setTextColor(BLACK);
                break;
            case 2:
                cell.setTextColor(BLUE);
                break;
            case 3:
                cell.setTextColor(GREEN);
                break;
            case 4:
                cell.setTextColor(YELLOW);
                break;
            default:
                cell.setTextColor(RED);
                break;
        }
    }

}