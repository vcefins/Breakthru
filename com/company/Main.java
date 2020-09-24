package com.company;
import com.company.State;
import com.company.SearchTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        // IF SIDE IS GOLD (true), CURRENT STATE TYPE IS ALWAYS GOLD1
        // IF SIDE IS SILVER (false), CURRENT STATE TYPE IS ALWAYS SILVER1
        boolean isSideGold = false;
        String type = isSideGold ? "GOLD1" : "SILVER1";    // cool

        int[][] map = new int[11][11];
        Game gameState = new Game(map, type);

        // Init board
        BoardSetup(map);


        // Instantiate map as State

        playOnePlyDeepGame(map, type);

        //for DEBUG purposes
        //currentState.moveset.move.forEach(move -> System.out.println(Arrays.toString(move)));
        //GenerateMovesforTile(new int[]{1, 3}, map);

        // Visualize
        //GUI(gameState.board);
    }


    // Fills the board to starting position (takes empty map)
    public static void BoardSetup(int[][] map){
        map[5][5] = 3;  //FLAGSHIP
        // Silver
        map[1][3] = map[1][4] = map[1][5] = map[1][6] = map[1][7] = 1;
        map[3][1] = map[4][1] = map[5][1] = map[6][1] = map[7][1] = 1;
        map[3][9] = map[4][9] = map[5][9] = map[6][9] = map[7][9] = 1;
        map[9][3] = map[9][4] = map[9][5] = map[9][6] = map[9][7] = 1;

        map[9][5] = 0;
        map[10][4] = 1;
        //Gold
        map[3][4] = map[3][5] = map[3][6] = 2;
        map[4][3] = map[5][3] = map[6][3] = 2;
        map[4][7] = map[5][7] = map[6][7] = 2;
        map[7][4] = map[7][5] = map[7][6] = 2;

        map[7][5] = 0;
        map[8][6] = 2;

        /*map = new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 2, 2, 2, 0, 0, 1, 0},
                {0, 1, 0, 2, 0, 0, 0, 2, 0, 1, 0},
                {0, 1, 0, 2, 0, 3, 0, 2, 0, 1, 0},
                {0, 1, 0, 2, 0, 0, 0, 2, 0, 1, 0},
                {0, 1, 0, 0, 2, 2, 2, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };*/
    }

    // Formats and outputs the board (takes map)
    public static void GUI(int[][] map){

        char[] alphabet = "abcdefghijk".toCharArray();
        int count = map.length;

        for (int i = 0; i < map.length; i++){
            String temp = "";
            for(int j = 0; j < map[i].length; j++){
                if (map[i][j] != 0){
                    temp = temp + "\t" + map[i][j];
                } else {
                    temp = temp + "\t." +
                            "";
                }
            }
            System.out.println(count + "\t" + temp);
            count--;
        }

        String letters = "\n\t\t";
        for(int i = 0; i < map.length ;i++)
        {
            letters = letters + alphabet[i] + "\t";
        }
        System.out.println(letters);
    }


    public static void playRandomGame(int[][] board, String type){
        State currentState = new State(board, type);
        int counter = 1;

        while(true) {

            currentState.exploreChildren();
            ArrayList<State> maps = currentState.prepareChildrenStatesforTree();

            Random random = new Random();
            int rand = random.nextInt(maps.size());

            // Select a possible state at random
            State newState = maps.get(rand);
            GUI(newState.board);

            // If the first move is a regular ship movement
            if (newState.stateType.equals("GOLD2") || newState.stateType.equals("SILVER2")) {
                ArrayList<State> maps2 = new ArrayList<>();

                newState.exploreChildren();
                maps2 = newState.prepareChildrenStatesforTree();

                int rand2 = random.nextInt(maps2.size());
                currentState = maps2.get(rand2);
                GUI(currentState.board);
            } else {
                currentState = newState;
            }

            System.out.println("\nEnd of Round " + counter);
            counter++;
            int win = currentState.isTerminalState();
            if(win == 1){
                System.out.println("\n\nGOLD WINS!!!");
                break;
            } else if(win == 2){
                System.out.println("\n\nSILVER WINS!!!");
                break;
            }

            System.out.println("Now it is " + currentState.stateType + "'s turn\n\n");
            /*Scanner keyboard = new Scanner(System.in);
            if (keyboard.nextLine().equals("exit")){
                break;*/
        }
    }

    public static void playOnePlyDeepGame(int[][] board, String type){
        State currentState = new State(board, type);
        int counter = 1;

        while(true) {
            // Expand current state
            currentState.exploreChildren();
            ArrayList<State> maps = currentState.prepareChildrenStatesforTree();

            // temporary State
            State newState = new State(board, type);

            if(type.equals("GOLD1") || type.equals("GOLD2")){
                // MAXIMIZE
                int maxScore = -8000;
                for(State state : maps){
                    int value = SearchTree.stateEvaluation(state);
                    if(value > maxScore){
                        newState = state;
                        maxScore = value;
                    }
                }
            } else {    // Silver Turn
                // MINIMIZE
                int minScore = 8000;
                for(State state : maps){
                    int value = SearchTree.stateEvaluation(state);
                    if(value < minScore){
                        newState = state;
                        minScore = value;
                    }
                }
            }


            GUI(newState.board);

            // IF there exists a SECOND MOVE
            if (newState.stateType.equals("GOLD2") || newState.stateType.equals("SILVER2")) {
                ArrayList<State> maps2 = new ArrayList<>();

                newState.exploreChildren();
                maps2 = newState.prepareChildrenStatesforTree();

                if(type.equals("GOLD1") || type.equals("GOLD2")){
                    // MAXIMIZE
                    int maxScore = -8000;
                    for(State state : maps){
                        int value = SearchTree.stateEvaluation(state);
                        if(value > maxScore){
                            currentState = state;
                            maxScore = value;
                        }
                    }
                } else {    // Silver Turn
                    // MINIMIZE
                    int minScore = 8000;
                    for(State state : maps){
                        int value = SearchTree.stateEvaluation(state);
                        if(value < minScore){
                            currentState = state;
                            minScore = value;
                        }
                    }
                }

                GUI(currentState.board);
            } else {
                currentState = newState;
            }

            System.out.println("\nEnd of Round " + counter);
            counter++;

            switch (currentState.isTerminalState()) {
                case 1:
                    System.out.println("\n\nGOLD WINS!!!");
                    return;
                case 2:
                    System.out.println("\n\nSILVER WINS!!!");
                    return;
            }

            System.out.println("Now it is " + currentState.stateType + "'s turn\n\n");
            /*Scanner keyboard = new Scanner(System.in);
            if (keyboard.nextLine().equals("exit")){
                break;*/
        }
    }




    // TO DO: CLONE ENTIRE RESPECTIVE ROW & COLUMN to decrease the number of calls made to MAP//
    // TO DO : PLEASE GET RID OF 4 LOOPS (DISGUSTING CODE)//
    // TESTING ONLY
    public static void GenerateMovesforTile(int[] coor, int[][] map){
        //int[] row = map[coor[0]][:];
        //int[] column = map[:][coor[1]];

        // Row index of Tile
        int x = coor[0];
        // Column index of Tile
        int y = coor[1];

        // Return available indeces in ROW and COLUMN
        ArrayList<Integer> r_row = new ArrayList<Integer>();
        ArrayList<Integer> r_column = new ArrayList<Integer>();

        // Search WEST
        for (int i = y-1; i >= 0; i--){
            if(map[x][i] != 0){
                break;
            } else {
                r_row.add(i);
            }
        }

        // Search EAST
        for (int i = y+1; i <= map.length; i++){
            if(map[x][i] != 0){
                break;
            } else {
                r_row.add(i);
            }
        }

        // Search NORTH
        for (int i = x-1; i >= 0; i--){
            if(map[i][y] != 0){
                break;
            } else {
                r_column.add(i);
            }
        }

        // Search SOUTH
        for (int i = x+1; i <= map.length; i++){
            //System.out.println("Now checking " + i + " : " + y);
            if(map[i][y] != 0){
                break;
            } else {
                //System.out.println("Add column: " + i);
                r_column.add(i);
            }
        }

        // DEBUG
        System.out.println(r_row);
        System.out.println(r_column);

        //ArrayList<Integer>[][] array = {{r_row}, {r_column}};
        //return array;
    }

}