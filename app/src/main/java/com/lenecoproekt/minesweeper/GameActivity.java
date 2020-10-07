package com.lenecoproekt.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    int height = 10;
    int width = 10;
    private GameObject [][] gameObjects = new GameObject[height][width];
    private int minesNumber = height*width / 10;
    private int flagCounter = minesNumber;
    private int closedTiles = height*width;
    private int countMines = 0;
    private Button[][] buttons = new Button[height][width];
    private boolean isGameStopped;
    private TextView mines;
    private TextView flags;
    private TextView score;
    private int scoreN = 0;
    private TextView time;
    private int sec = 0;
    private Chronometer mChronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if (getIntent().getExtras() != null) {
            height = getIntent().getExtras().getInt("HEIGHT");
            width = getIntent().getExtras().getInt("WIDTH");
            minesNumber = height*width * getIntent().getExtras().getInt("MINES") / 100;
            closedTiles = height*width;
        }
        TableLayout gameField = findViewById(R.id.gameField);
        gameField.setStretchAllColumns(true);
        gameField.setShrinkAllColumns(true);
        mines = findViewById(R.id.minesNumber);
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
        time = findViewById(R.id.time);
        loadMines();
        loadNumbers();
        mines.setText(String.valueOf(countMines));
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
//        gameField.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
//                TableLayout.LayoutParams.MATCH_PARENT));

        for (int i = 0; i < height; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            for (int j = 0; j < width; j++) {
                final Button button = new Button(this);
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
                        if (!isGameStopped) {
                            if (!gameObjects[finalI][finalJ].isOpen) {
                                if (!gameObjects[finalI][finalJ].isFlag) {
                                    if (flagCounter > 0) {
                                        button.setText("\uD83D\uDC31");
                                        gameObjects[finalI][finalJ].isFlag = true;
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
                        return true;
                        }
                });
            }
            gameField.addView(tableRow, i);
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
        Toast.makeText(this, R.string.gameOver, Toast.LENGTH_LONG).show();
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
        Toast.makeText(this, R.string.success, Toast.LENGTH_LONG).show();
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
                    boolean isMine = Math.random() * 10 < 1;
                    if (isMine) countMines++;
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
                if (y < 0 || y >= height) {
                    continue;
                }
                if (x < 0 || x >= width) {
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