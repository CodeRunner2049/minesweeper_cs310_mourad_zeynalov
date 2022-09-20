package com.example.minesweeper_cs310_mourad_zeynalov;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.os.Handler;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMN_COUNT = 8;
    private static final int ROW_COUNT = 10;

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;

    public int mines[][];
    public char trueGame[][];
    public char currentGame[][];
    public int movesLeft = ROW_COUNT * COLUMN_COUNT - 4;
    public boolean gameOver = false;
    public boolean flagging = false;
    public int flagCount = 4;
    private int clock = 0;
    private boolean running = false;


    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cell_tvs = new ArrayList<TextView>();

        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        LayoutInflater li = LayoutInflater.from(this);
        for (int i = 0; i<=9; i++) {
            for (int j=0; j<=7; j++) {
                TextView tv = (TextView) li.inflate(R.layout.custom_cell_layout, grid, false);
                //tv.setText(String.valueOf(i)+String.valueOf(j));
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.parseColor("lime"));
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
            }
        }

        TextView tvb = (TextView) findViewById(R.id.textViewButton01);
        tvb.setOnClickListener(this::onClickFlag);

        mines = new int[4][2];
        trueGame = new char[ROW_COUNT][COLUMN_COUNT];
        currentGame = new char[ROW_COUNT][COLUMN_COUNT];

        initialise();
        placeMines();
    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }


    private void flipTextView(int r, int c) {
        TextView tv = cell_tvs.get(r*COLUMN_COUNT+c);
        tv.setTextColor(Color.GRAY);
        tv.setBackgroundColor(Color.LTGRAY);
    }

    public void onClickTV(View view){
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;
        if (!running) {runTimer();}
        if (gameOver == false)
        {
            if (!flagging) {
                gameOver = recurseDown(currentGame, i, j);
                if ((gameOver == false) && (movesLeft == 0)) {
                    // Switch to result page
                    String message = "Used " + clock + " seconds. \n You won. \n Good Job!";

                    Intent intent = new Intent(this, WinPage.class);
                    intent.putExtra("com.example.sendmessage.MESSAGE", message);

                    startActivity(intent);
                    gameOver = true;
                }
            }
            else {
                if (tv.getText() != "🚩") {
                    cell_tvs.get(i*COLUMN_COUNT+j).setText("🚩");
                    flagCount--;
                } else {
                    cell_tvs.get(i*COLUMN_COUNT+j).setText("");
                    flagCount++;
                }
                TextView tvfc = (TextView) findViewById(R.id.textViewFlagCount);
                tvfc.setText(String.valueOf(flagCount));
            }
        }
    }

    public void onClickFlag(View view){
        TextView tv = (TextView) view;
        if(tv.getText() == "@string/flag" || tv.getText() == "🚩") {
            flagging = false;
            tv.setText("⛏");
        }
        else {
            flagging = true;
            tv.setText("🚩");
        }
    }

    // A Recursive Function to play the Minesweeper Game
    private boolean recurseDown(char currentGame[][], int r, int c)
    {
        // Base Case of Recursion
        if (currentGame[r][c] != '-') {
            return (false);
        }


        if (trueGame[r][c] == '*')
        {
            currentGame[r][c]='*';
            for (int i=0; i<4; i++) {
                cell_tvs.get(mines[i][0]*COLUMN_COUNT+mines[i][1]).setText("💣");
            }
            running = false;
            return true;
        }

        else
        {
            int count = nearbyMines(r, c);
            movesLeft--;

            currentGame[r][c] = (char)(count + '0');

            flipTextView(r, c);
            if (count == 0) {
                cell_tvs.get(r*COLUMN_COUNT+c).setText("");
            }
            else {
                cell_tvs.get(r*COLUMN_COUNT+c).setText(String.valueOf(count));
            }

            if (count == 0)
            {
                // Only process this cell if this is a valid one
                if (inBounds (r-1, c))
                {
                    if (!safeHere (r-1, c)) recurseDown(currentGame, r-1, c);
                }
                if (inBounds (r+1, c))
                {
                    if (!safeHere (r+1, c)) recurseDown(currentGame, r+1, c);
                }
                if (inBounds (r, c+1))
                {
                    if (!safeHere (r, c+1)) recurseDown(currentGame, r, c+1);
                }
                if (inBounds (r, c-1) == true)
                {
                    if (!safeHere (r, c-1)) recurseDown(currentGame, r, c-1);
                }
                if (inBounds (r-1, c+1))
                {
                    if (!safeHere (r-1, c+1)) recurseDown(currentGame, r-1, c+1);
                }
                if (inBounds (r-1, c-1))
                {
                    if (!safeHere (r-1, c-1)) recurseDown(currentGame, r-1, c-1);
                }
                if (inBounds (r+1, c+1))
                {
                    if (!safeHere (r+1, c+1)) recurseDown(currentGame, r+1, c+1);                }
                if (inBounds (r+1, c-1))
                {
                    if (!safeHere (r+1, c-1)) recurseDown(currentGame, r+1, c-1);
                }
            }

            return false;
        }
    }


    private boolean safeHere (int r, int c)
    {
        if (trueGame[r][c] == '*')
            return true;
        else
            return false;
    }

    private boolean inBounds(int r, int c)
    {
        // Returns true if r number and cumn number
        // is in range
        return (r >= 0) && (r < 10) &&
                (c >= 0) && (c < 8);
    }

    private int nearbyMines(int r, int c)
    {

        int i;
        int count = 0;

        if (inBounds (r-1, c)) {
            if (safeHere (r-1, c)) count++;
        }
        if (inBounds (r+1, c))
        {
            if (safeHere (r+1, c)) count++;
        }
        if (inBounds (r, c+1))
        {
            if (safeHere (r, c+1)) count++;
        }

        if (inBounds (r, c-1))
        {
            if (safeHere (r, c-1)) count++;
        }

        if (inBounds (r-1, c+1))
        {
            if (safeHere (r-1, c+1)) count++;
        }
        if (inBounds (r-1, c-1))
        {
            if (safeHere (r-1, c-1)) count++;
        }
        if (inBounds (r+1, c+1))
        {
            if (safeHere (r+1, c+1)) count++;
        }
        if (inBounds (r+1, c-1))
        {
            if (safeHere (r+1, c-1)) count++;
        }

        return (count);
    }


    private void placeMines()
    {
        Random rand = new Random();
        for (int i=0; i<4; )
        {
            int random = rand.nextInt(ROW_COUNT * COLUMN_COUNT);
            int x = random / COLUMN_COUNT;
            int y = random % COLUMN_COUNT;

            mines[i][0]= x;
            mines[i][1] = y;

            trueGame[mines[i][0]][mines[i][1]] = '*';
            /*TextView tv01 = (TextView) findViewById(R.id.textView01);
            cell_tvs.set(x * COLUMN_COUNT + y, tv01);*/
            i++;
        }

        return;
    }

    private void initialise()
    {
        for (int i=0; i<ROW_COUNT; i++)
        {
            for (int j=0; j<COLUMN_COUNT; j++)
            {
                currentGame[i][j] = trueGame[i][j] = '-';
            }
        }

        return;
    }

    public void runTimer() {
        final TextView timeView = (TextView) findViewById(R.id.textViewTimer);
        final Handler handler = new Handler();
        running = true;

        handler.post(new Runnable() {
            @Override
            public void run() {
                int seconds = clock%60;
                timeView.setText(String.valueOf(seconds));

                if (running) {
                    clock++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
}
