package com.example.chess_knight_path;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

public class FindPath extends AsyncTask<Void, int[][], ArrayList<ArrayList<Node>>> {

    private int size, xSource, ySource, xDestination, yDestination;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private Dialog dialog;
    private ArrayList<ArrayList<Node>> paths;
    private ArrayList<Node> queue;

    public FindPath(int size, int xSource, int ySource, int xDestination, int yDestination, Context context) {
        this.size = size;
        this.xSource = xSource;
        this.ySource = ySource;
        this.xDestination = xDestination;
        this.yDestination = yDestination;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        /*
         * paths is the ArrayList where all the possible paths are being stored
         * queue is the ArrayList where the current path is being stored
         */
        paths = new ArrayList<>();
        queue = new ArrayList<>();

        dialog = new Dialog(context);
        dialog.setTitle("Searching for possible paths...");
        dialog.show();
    }

    @Override
    protected ArrayList<ArrayList<Node>> doInBackground(Void... voids) {
        return knight_algorithm();
    }

    @Override
    protected void onPostExecute(ArrayList<ArrayList<Node>> o) {
        super.onPostExecute(o);
        dialog.dismiss();
    }

    /*
     * A utility function to check if i,j are
     * valid indexes for size*size chessboard
     */
    private boolean isSafe(int x, int y) {
        return (x >= 0 && x < size && y >= 0 &&
                y < size && !queue.contains(new Node(x, y)));
    }

    /*
     * This function finds all the possible paths
     * using Backtracking. This function mainly
     * uses findPaths() to solve the problem. It
     * returns -1 if no path can reach the destination in 3 moves,
     * otherwise it returns 0 or 1 and returns the paths to Main Activity.
     */
    private ArrayList<ArrayList<Node>> knight_algorithm() {

        /*
         * xMove[] and yMove[] define next move of Knight.
         * xMove[] is for next value of x coordinate
         * yMove[] is for next value of y coordinate
         */
        int[] xMove = {2, 1, -1, -2, -2, -1, 1, 2};
        int[] yMove = {1, 2, 2, 1, -1, -2, -2, -1};

        /*
         * Start from xSource, ySource and explore all possible paths
         * using findPaths(
         */
        if (findPaths(xSource, ySource, 1, xMove, yMove) == -1) {
            return null;
        } else
            return paths;
    }

    /*
     * A recursive function to explore all possible paths
     * 3 moves away from the source square. In each move,
     * the coordinates of the squares in the path, are stored
     * in queue.
     */
    private int findPaths(int x, int y, int move,
                          int[] xMove,
                          int[] yMove) {
        int k, next_x, next_y;

        /* If we have reached the destination with the 3rd move
         * return 1
         */
        if (move == 4 && x == xDestination && y == yDestination) {
            return 1;

        } else if (move < 4) {
            /* Try all next moves from the current
             * coordinate x, y
             */
            for (k = 0; k < 8; k++) {
                next_x = x + xMove[k];
                next_y = y + yMove[k];
                if (isSafe(next_x, next_y)) {
                    addPathSquares(k, x, y, xMove, yMove, next_x, next_y);
                    int result = findPaths(next_x, next_y, move + 1, xMove, yMove);
                    if (result == 1) {
                        foundAPath();
                    } else {
                        deleteMove();
                    }
                }
            }
            return 0;
        }
        return -1;
    }

    /*
     * This function removes from the queue the last move
     * in it
     */
    private void deleteMove() {
        for (int i = 0; i < 3; i++) {
            queue.remove(queue.size() - 1);
        }
    }

    /*
     * This function adds to a list of possible paths the new path found
     * and removes the last move from the queue
     */
    private void foundAPath() {
        paths.add(new ArrayList<Node>());
        for (int i = 0; i < queue.size(); i++) {
            paths.get(paths.size() - 1).add(queue.get(i));
        }

        deleteMove();
    }

    /*
     * This function adds to the queue the squares of the current move
     */
    private void addPathSquares(int k, int x, int y, int[] xMove, int[] yMove, int next_x, int next_y) {

        if (yMove[k] == 1 || yMove[k] == -1) {
            y = y + yMove[k];
            queue.add(new Node(x, y));
            queue.add(new Node(x + (xMove[k] / 2), y));

        } else {
            x = x + xMove[k];
            queue.add(new Node(x, y));
            queue.add(new Node(x, y + (yMove[k] / 2)));
        }
        queue.add(new Node(next_x, next_y));
    }
}
