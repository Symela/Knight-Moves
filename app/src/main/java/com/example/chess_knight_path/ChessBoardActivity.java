package com.example.chess_knight_path;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ChessBoardActivity extends AppCompatActivity {

    private int size;
    private int[][] pressed;
    private int boardSize;
    private boolean ableToProceedFlag;
    private Button goButton, resetButton;
    private boolean sourceFlag, destinationFlag;
    public TableLayout tableLayout;
    private int n = 64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_board);

        Intent intent = getIntent();

        /* Get selected size from Main Activity */
        if (intent.hasExtra("size")) {
            size = intent.getIntExtra("size", 7);
        }

        sourceFlag = false;
        destinationFlag = false;
        ableToProceedFlag = false;

        /* Initialization of source and destination squares coordinates */
        pressed = new int[][]{
                new int[]{-1, -1},
                new int[]{-1, -1}
        };

        /* Get screen's height and width */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        if (height < width) boardSize = height;
        else boardSize = width;

        tableLayout = findViewById(R.id.dimensions);
        goButton = findViewById(R.id.start_button);
        goButton.setEnabled(ableToProceedFlag);
        resetButton = findViewById(R.id.reset_button);
        resetButton.setEnabled(ableToProceedFlag);
        final TextView title = findViewById(R.id.textView);
        final TextView results = findViewById(R.id.pathsFound);
        results.setVisibility(View.GONE);

        /* reset button resets all the squares on the chess board */
        resetButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {

                title.setVisibility(View.VISIBLE);
                results.setVisibility(View.GONE);

                View view, cell;

                if (sourceFlag && destinationFlag) {

                    sourceFlag = false;
                    destinationFlag = false;

                    for (int i = 1; i < size; i++) {
                        for (int j = 1; j < size; j++) {
                            view = tableLayout.getChildAt(i);
                            cell = ((TableRow) view).getChildAt(j);
                            cell.setBackground(getResources().getDrawable(R.drawable.back));
                        }
                    }
                } else {

                    if (pressed[0][0] > 0) {
                        view = tableLayout.getChildAt(pressed[0][0]);
                        cell = ((TableRow) view).getChildAt(pressed[0][1]);
                        cell.setBackground(getResources().getDrawable(R.drawable.back));
                        sourceFlag = false;
                    }

                    if (pressed[1][0] > 0) {
                        view = tableLayout.getChildAt(pressed[1][0]);
                        cell = ((TableRow) view).getChildAt(pressed[1][1]);
                        cell.setBackground(getResources().getDrawable(R.drawable.back));
                        destinationFlag = false;
                    }
                }
            }
        });

        /*
         * Calls the FindPath Async Task that runs the algorithm
         * for all possible paths from source to destination
         * and then prints the results
         */
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setVisibility(View.GONE);
                goButton.setEnabled(false);
//                Log.e("go", "go has been pressed");
                FindPath task = new FindPath(size - 1, pressed[0][1] - 1, pressed[0][0] - 1, pressed[1][1] - 1, pressed[1][0] - 1, ChessBoardActivity.this);
                task.execute();
                try {
                    ArrayList<ArrayList<Node>> table = task.get();
                    if (table != null && table.size() > 0)
                        printSolution(table, results);
                    else {
                        results.setText(getString(R.string.no_path_message));
                        results.setVisibility(View.VISIBLE);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        createChessBoard();
    }

    /*
     * Generates a table size*size where the height of each row
     * and the width of each column is equal to the width of
     * the screen divided by the given size. The first row
     * of the table shows the name of each column and the first
     * column of the table shows the number of each row.
     */
    @SuppressLint("NewApi")
    private void createChessBoard() {
        LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(boardSize, (boardSize / size));

        for (int j = 0; j < size; j++) {
            /* create a table row */
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(tableRowParams);

            /* add views to the row */
            for (int i = 0; i < size; i++) {
                if (j == 0) {
                    if (i == 0) {
                        /* create cell element - textview */
                        TextView tv = new TextView(this);

                        /* set params for cell elements */
                        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(boardSize, boardSize / size);
                        cellParams.weight = 1;
                        tv.setLayoutParams(cellParams);
                        tableRow.setGravity(Gravity.CENTER);
                        tableRow.addView(tv);
                    } else {
                        /* create cell element - textview */
                        TextView tv = new TextView(this);

                        /* set params for cell elements */
                        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(boardSize, boardSize / size);
                        cellParams.weight = 1;
                        tv.setLayoutParams(cellParams);
                        int letter = n + i;
                        char l = (char) letter;
                        tv.setText(String.valueOf(l));
                        tv.setGravity(Gravity.CENTER);
                        tv.setTextSize(20);
                        tableRow.setGravity(Gravity.CENTER);
                        tableRow.addView(tv);
                    }
                } else {
                    if (i == 0) {
                        /* create cell element - textview */
                        TextView tv = new TextView(this);

                        /* set params for cell elements */
                        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(boardSize, boardSize / size);
                        cellParams.weight = 1;
                        tv.setLayoutParams(cellParams);
                        tv.setText(String.valueOf(size - j));
                        tv.setGravity(Gravity.CENTER);
                        tv.setTextSize(20);
                        tableRow.setGravity(Gravity.CENTER);
                        tableRow.addView(tv);
                    } else {
                        /* create cell element - textview */
                        TextView tv = new TextView(this);
                        tv.setBackground(getBaseContext().getResources().getDrawable(R.drawable.back));
                        final int finalJ = j;
                        final int finalI = i;
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!sourceFlag) {
                                    sourceFlag = true;
                                    pressed[0][0] = finalJ;
                                    pressed[0][1] = finalI;
                                    v.setBackgroundColor(getColor(R.color.source));
                                    ableToProceedFlag = true;
                                    resetButton.setEnabled(ableToProceedFlag);

                                } else if (!destinationFlag) {
                                    destinationFlag = true;
                                    pressed[1][0] = finalJ;
                                    pressed[1][1] = finalI;
                                    v.setBackgroundColor(getColor(R.color.destination));
                                    goButton.setEnabled(ableToProceedFlag);

                                }
                            }
                        });

                        /* set params for cell elements */
                        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(boardSize, boardSize / size);
                        cellParams.weight = 1;
                        tv.setLayoutParams(cellParams);
                        tableRow.setGravity(Gravity.CENTER);
                        tableRow.addView(tv);
                    }
                }
            }

            /* add the row to the table */
            tableLayout.addView(tableRow);
        }
    }

    /*
     * Prints the paths found by the algorithm
     */
    @SuppressLint("NewApi")
    private void printSolution(ArrayList<ArrayList<Node>> paths, TextView results) {

        StringBuilder text = new StringBuilder("Paths Found:");

        for (int x = 0; x < paths.size(); x++) {
            int letter = n + pressed[0][1];
            char l = (char) letter;
            text.append("\n\n").append(x + 1).append(")\t").append(l).append(size - pressed[0][0]);

            for (int y = 0; y < paths.get(x).size(); y++) {
                letter = n + paths.get(x).get(y).x + 1;
                l = (char) letter;
                if ((y + 1) % 3 == 0 && (y + 1) < paths.get(x).size())
                    text.append(" -> ").append(l).append(size - paths.get(x).get(y).y - 1).append("\n\t\t\t").append(l).append(size - paths.get(x).get(y).y - 1);
                else
                    text.append(" -> ").append(l).append(size - paths.get(x).get(y).y - 1);
            }

        }
        results.setText(text.toString());
        results.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("size", size);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        size = savedInstanceState.getInt("size");
    }
}
