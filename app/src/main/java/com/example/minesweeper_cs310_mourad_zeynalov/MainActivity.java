package com.example.minesweeper_cs310_mourad_zeynalov;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.lang.Math;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMN_COUNT = 8;
    private static final int ROW_COUNT = 10;

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;

    public int mines[][];
    public char realBoard[][];
    public char myBoard[][];
    public int movesLeft = ROW_COUNT * COLUMN_COUNT - 4;
    public Boolean gameOver = false;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cell_tvs = new ArrayList<TextView>();

        initialise();
        placeMines();

        mines = new int[4][2];
        realBoard = new char[10][8];
        myBoard = new char[10][8];

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

    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }


    private void flipTextView(int row, int col) {
        TextView tv = cell_tvs.get(row*COLUMN_COUNT+col);
        tv.setTextColor(Color.GRAY);
        tv.setBackgroundColor(Color.LTGRAY);
    }

    public void onClickTV(View view){
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;
        if (gameOver == false)
        {
            gameOver = playMinesweeperUtil (myBoard, i, j, movesLeft);
            if ((gameOver == false) && (movesLeft == 0))
            {
            // Switch to result page
                gameOver = true;
            }
        }
    }

    // A Recursive Function to play the Minesweeper Game
    private Boolean playMinesweeperUtil(char myBoard[][], int row, int col, int movesLeft)
    {
        // Base Case of Recursion
        if (myBoard[row][col] != '-')
            return (false);

        int i, j;

        // You opened a mine
        // You are going to lose
        if (realBoard[row][col] == '*')
        {
            myBoard[row][col]='*';

            for (i=0; i<4; i++)
                myBoard[mines[i][0]][mines[i][1]]='*';

            return true;
        }

        else
        {
            // Calculate the number of adjacent mines and put it
            // on the board
            int count = countAdjacentMines(row, col);
            movesLeft--;

            myBoard[row][col] = (char)(count + '0');

            flipTextView(row, col);
            cell_tvs.get(row*COLUMN_COUNT+col).setText(String.valueOf(count));

            if (count != 0)
            {
                // Only process this cell if this is a valid one
                if (isValid (row-1, col))
                {
                    if (isMine (row-1, col))
                        playMinesweeperUtil(myBoard, row-1, col, movesLeft);
                }

                if (isValid (row+1, col))
                {
                    if (isMine (row+1, col))
                        playMinesweeperUtil(myBoard, row+1, col, movesLeft);
                }

                if (isValid (row, col+1))
                {
                    if (isMine (row, col+1))
                        playMinesweeperUtil(myBoard, row, col+1, movesLeft);
                }

                if (isValid (row, col-1) == true)
                {
                    if (isMine (row, col-1))
                        playMinesweeperUtil(myBoard, row, col-1, movesLeft);
                }

                if (isValid (row-1, col+1))
                {
                    if (isMine (row-1, col+1))
                        playMinesweeperUtil(myBoard, row-1, col+1, movesLeft);
                }

                if (isValid (row-1, col-1))
                {
                    if (isMine (row-1, col-1))
                        playMinesweeperUtil(myBoard, row-1, col-1, movesLeft);
                }

                if (isValid (row+1, col+1))
                {
                    if (isMine (row+1, col+1))
                        playMinesweeperUtil(myBoard, row+1, col+1, movesLeft);
                }

                if (isValid (row+1, col-1))
                {
                    if (isMine (row+1, col-1))
                        playMinesweeperUtil(myBoard, row+1, col-1, movesLeft);
                }
            }

            return false;
        }
    }


    private Boolean isMine (int row, int col)
    {
        if (realBoard[row][col] == '*')
            return true;
        else
            return false;
    }

    private Boolean isValid(int row, int col)
    {
        // Returns true if row number and column number
        // is in range
        return (row >= 0) && (row < 10) &&
                (col >= 0) && (col < 8);
    }

    private int countAdjacentMines(int row, int col)
    {

        int i;
        int count = 0;

        if (isValid (row-1, col)) {
            if (isMine (row-1, col))
                count++;
        }
        if (isValid (row+1, col))
        {
            if (isMine (row+1, col))
                count++;
        }
        if (isValid (row, col+1))
        {
            if (isMine (row, col+1))
                count++;
        }

        if (isValid (row, col-1))
        {
            if (isMine (row, col-1))
                count++;
        }

        if (isValid (row-1, col+1))
        {
            if (isMine (row-1, col+1))
                count++;
        }
        if (isValid (row-1, col-1))
        {
            if (isMine (row-1, col-1))
                count++;
        }
        if (isValid (row+1, col+1))
        {
            if (isMine (row+1, col+1))
                count++;
        }
        if (isValid (row+1, col-1))
        {
            if (isMine (row+1, col-1))
                count++;
        }

        return (count);
    }


    // A Function to place the mines randomly
    // on the board
    private void placeMines()
    {
        Boolean mark[] = new Boolean[10*8];

        // Continue until all random mines have been created.
        for (int i=0; i<4; )
        {
            int random = (int)Math.random() % (10*8);
            int x = random / 8;
            int y = random % 8;

            // Add the mine if no mine is placed at this
            // position on the board
            if (mark[random] == false)
            {
                // Row Index of the Mine
                mines[i][0]= x;
                // Column Index of the Mine
                mines[i][1] = y;

                // Place the mine
                realBoard[mines[i][0]][mines[i][1]] = '*';
                mark[random] = true;
                i++;
            }
        }

        return;
    }

    // A Function to initialise the game
    private void initialise()
    {
        // Assign all the cells as mine-free
        for (int i=0; i<10; i++)
        {
            for (int j=0; j<8; j++)
            {
                myBoard[i][j] = realBoard[i][j] = '-';
            }
        }

        return;
    }

}
