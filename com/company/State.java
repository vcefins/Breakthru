package com.company;

import java.util.ArrayList;
import java.util.Arrays;

import com.company.Moveset;


// State: Board state class to store 2D array board and generate possible moves
public class State {
    int[][] board;
    ArrayList<State> children;
    int value;   //State's evaluation result
    Moveset moveset = new Moveset();
    int[] previousShiptoMove = new int[2];
    int[] flagshipCoor;
    int goldShipCurrentNum;
    int silverShipCurrentNum;


    // GOLD1, GOLD2, SILVER1, SILVER2
    // Dictates what is allowed to happen in this ply
    String stateType;

    public State(int[][] map, String type){
        board = map;
        stateType = type;
        setFlagshipCoor_countShips();
    }

    // Custom constructor for second movement (excludes previously moved ship from moving again)
    public State(int[][] map, String type, int[] previousShiptoMove){
        board = map;
        stateType = type;
        this.previousShiptoMove = previousShiptoMove;
        setFlagshipCoor_countShips();
    }

    public void setFlagshipCoor_countShips() {
        goldShipCurrentNum = 0;
        silverShipCurrentNum = 0;
        boolean flag = false;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                int ship = board[i][j];
                if ( ship == 1 ){ silverShipCurrentNum++; }
                else if ( ship == 2 ){ goldShipCurrentNum++; }

                // Find Flagship
                if (ship == 3) {
                    flagshipCoor = new int[]{i, j};
                    // return;
                    goldShipCurrentNum++;
                    flag = true;
                }
            }
        }
        if(!flag){flagshipCoor = new int[]{};}
    }

    // Call this method to initiate move search
    public void exploreChildren(){
        if (stateType.equals("GOLD1") || stateType.equals("GOLD2")) {
            FindGoldandGenerate();
        } else {
            FindSilverandGenerate();
        }
    }

    // When called, pulls all legal moves found [with exploreChildren] and generates all resulting board configurations
    // MOVE PRIORITIZATION
    // MOVE LIMITATIONS
    public ArrayList<State> prepareChildrenStatesforTree(){
        ArrayList<State> maps = new ArrayList<>();
        switch (stateType) {
            case "GOLD1":
                moveset.flagship.forEach(move -> maps.add(playMove(move, "SILVER1")));
                moveset.capture.forEach(move -> maps.add(playMove(move, "SILVER1")));
                moveset.move.forEach(move -> maps.add(playMoveFirstMovement(move, "GOLD2", new int[]{move[2], move[3]})));

                break;
            case "SILVER1":
                moveset.capture.forEach(move -> maps.add(playMove(move, "GOLD1")));
                moveset.move.forEach(move -> maps.add(playMoveFirstMovement(move, "SILVER2", new int[]{move[2], move[3]})));

                break;
            case "GOLD2":
                moveset.move.forEach(move -> maps.add(playMove(move, "SILVER1")));
                break;
            case "SILVER2":
                moveset.move.forEach(move -> maps.add(playMove(move, "GOLD1")));
                break;
        }

        return maps;    // These are the children states (type:State)
    }

    // Terrible way to play a move (returns a new State instance)       // Added ship type to update Flagship Coordinate for efficiency i nterminal state check
    public State playMove(Integer[] move, String nextStateType){
        int x = move[0];
        int y = move[1];
        int x_new = move[2];
        int y_new = move[3];

        // I CAN'T BELIEVE .CLONE DOESN'T WORK
        int[][] temp = new int[board.length][board[0].length];
        for(int i=0; i<board.length; i++){
            for(int j=0; j<board[i].length; j++){
                temp[i][j]=board[i][j];
            }
        }

        // Switch to move
        int ship = temp[x][y];
        temp[x][y] = 0;
        temp[x_new][y_new] = ship;

        return new State(temp, nextStateType);
    }

    // Terrible way to play a move 2 electric bugaloo, only for second part of the movement action (returns a new State instance)
    public State playMoveFirstMovement(Integer[] move, String nextStateType, int[] lastShipMove){
        int x = move[0];
        int y = move[1];
        int x_new = move[2];
        int y_new = move[3];
        int[][] temp = new int[board.length][board[0].length];

        for(int i=0; i<board.length; i++){
            for(int j=0; j<board[i].length; j++){
                temp[i][j]=board[i][j];
            }
        }

        int ship = temp[x][y];
        temp[x][y] = 0;
        temp[x_new][y_new] = ship;

        return new State(temp, nextStateType, lastShipMove);
    }

    // Find GOLD ships and calculate all legal moves
    // P.S Generates capture only if 'GOLD1' && generates flagship moves only in 'GOLD1'
    public void FindGoldandGenerate(){
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == 2) {
                    // Skip the found ship if it's the one moved in first movement.
                    if (previousShiptoMove.length != 0){
                        if (previousShiptoMove[0] == i && previousShiptoMove[1] == j){
                            continue;
                        }
                    }

                    int[] coor = {i, j};
                    if ( stateType.equals("GOLD1") ){
                        GenerateCaptureGold(coor, 2);
                    }
                    GenerateMovementGold(coor, 2);
                } else if (board[i][j] == 3) {  //FLAGSHIP
                    if ( stateType.equals("GOLD1") ) {
                        int[] coor = {i, j};
                        GenerateCaptureGold(coor, 3);
                        GenerateMovementGold(coor, 3);
                    }
                }
            }
        }
    }

    // Find SILVER ships and calculate all legal moves
    // P.S Generates capture only if 'SILVER1'
    public void FindSilverandGenerate(){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board.length; j++){
                if (board[i][j] == 1) {
                    // Skip the found ship if it's the one moved in first movement.
                    if (previousShiptoMove.length != 0){
                        if (previousShiptoMove[0] == i && previousShiptoMove[1] == j){
                            continue;
                        }
                    }
                    int[] coor = {i, j};
                    if ( stateType.equals("SILVER1") ){
                        GenerateCaptureSilver(coor);
                    }
                    GenerateMovementSilver(coor);
                }
            }
        }
    }

    // Movement for a GOLD ship
    // P.s Flag Suicide isn't a legal move
    public void GenerateMovementGold(int[] coor, int ship) {
        // Row index of Tile
        int x = coor[0];
        // Column index of Tile
        int y = coor[1];

        // Search WEST
        for (int i = y-1; i >= 0; i--){
            if(board[x][i] != 0){
                break;
            } else {
                if(ship == 2){
                    moveset.move.add(new Integer[]{x, y, x, i});   //Move --> [x, y -> x, i]
                } else {
                    if(IsFlagSafe(coor)){
                        moveset.flagship.add(new Integer[]{x, y, x, i});
                    }
                }
            }
        }

        // Search EAST
        for (int i = y+1; i < board.length; i++){
            if(board[x][i] != 0){
                break;
            } else {
                if(ship == 2){
                    moveset.move.add(new Integer[]{x, y, x, i});
                } else {
                    moveset.flagship.add(new Integer[]{x, y, x, i});
                }
            }
        }

        // Search NORTH
        for (int i = x-1; i >= 0; i--){
            if(board[i][y] != 0){
                break;
            } else {
                if(ship == 2){
                    moveset.move.add(new Integer[]{x, y, i, y});
                } else {
                    moveset.flagship.add(new Integer[]{x, y, i, y});
                }
            }
        }

        // Search SOUTH
        for (int i = x+1; i < board.length; i++){
            //System.out.println("Now checking " + i + " : " + y);
            if(board[i][y] != 0){
                break;
            } else {
                //System.out.println("Add column: " + i);
                if(ship == 2){
                    moveset.move.add(new Integer[]{x, y, i, y});
                } else {
                    moveset.flagship.add(new Integer[]{x, y, i, y});
                }
            }
        }
    }

    // Movement for a SILVER ship
    public void GenerateMovementSilver(int[] coor) {
        // Row index of Tile
        int x = coor[0];
        // Column index of Tile
        int y = coor[1];

        // Search WEST
        for (int i = y-1; i >= 0; i--){
            if(board[x][i] != 0){
                break;
            } else {
                moveset.move.add(new Integer[]{x, y, x, i});   //Move --> [x, y -> x, i]
            }
        }

        // Search EAST
        for (int i = y+1; i < board.length; i++){
            if(board[x][i] != 0){
                break;
            } else {
                moveset.move.add(new Integer[]{x, y, x, i});
            }
        }

        // Search NORTH
        for (int i = x-1; i >= 0; i--){
            if(board[i][y] != 0){
                break;
            } else {
                moveset.move.add(new Integer[]{x, y, i, y});
            }
        }

        // Search SOUTH
        for (int i = x+1; i < board.length; i++){
            //System.out.println("Now checking " + i + " : " + y);
            if(board[i][y] != 0){
                break;
            } else {
                //System.out.println("Add column: " + i);
                moveset.move.add(new Integer[]{x, y, i, y});
            }
        }

    }

    // Capture move for a GOLD ship
    // P.s Flag Suicide isn't a legal move
    public void GenerateCaptureGold(int[] coor, int ship) {
        // Row index of Tile
        int x = coor[0];
        // Column index of Tile
        int y = coor[1];

        if (x != 0) {
            if (y != 0) {
                // try to capture x-1 , y-1
                if (board[x - 1][y - 1] == 1) {
                    if (ship == 3 && IsFlagSafe(new int[]{x - 1, y - 1})) {
                        moveset.capture.add(new Integer[]{x, y, x - 1, y - 1});
                    } else {
                        moveset.capture.add(new Integer[]{x, y, x - 1, y - 1});
                    }
                }
            }
            if (y != board.length - 1) {
                // try to capture x-1 , y+1
                if (board[x - 1][y + 1] == 1) {
                    if (ship == 3 && IsFlagSafe(new int[]{x - 1, y + 1})) {
                        moveset.capture.add(new Integer[]{x, y, x - 1, y + 1});
                    } else {
                        moveset.capture.add(new Integer[]{x, y, x - 1, y + 1});
                    }
                }
            }
        }
        if (x != board.length - 1) {
            if (y != 0) {
                // try to capture x+1 , y-1
                if (board[x + 1][y - 1] == 1) {
                    if (ship == 3 && IsFlagSafe(new int[]{x + 1, y - 1})) {
                        moveset.capture.add(new Integer[]{x, y, x + 1, y - 1});
                    } else {
                        moveset.capture.add(new Integer[]{x, y, x + 1, y - 1});
                    }
                }
                if (y != board.length - 1) {
                    // try to capture x+1 , y+1
                    if (board[x + 1][y + 1] == 1) {
                        if(ship == 3 && IsFlagSafe(new int[]{x + 1, y + 1})){
                            moveset.capture.add(new Integer[]{x, y, x+1, y+1});
                        } else {
                            moveset.capture.add(new Integer[]{x, y, x + 1, y + 1});
                        }
                    }
                }
            }
        }
    }

    // Capture move for a SILVER ship
    public void GenerateCaptureSilver(int[] coor) {
        // Row index of Tile
        int x = coor[0];
        // Column index of Tile
        int y = coor[1];

        if (x != 0){
            if(y != 0){
                // try to capture x-1 , y-1
                if(board[x-1][y-1] == 2){
                    moveset.capture.add(new Integer[]{x, y, x-1, y-1});
                } else if (board[x-1][y-1] == 3){
                    moveset.flagCapture.add(new Integer[]{x, y, x-1, y-1});
                }
            }
            if(y != board.length-1){
                // try to capture x-1 , y+1
                if(board[x-1][y+1] == 2){
                    moveset.capture.add(new Integer[]{x, y, x-1, y+1});
                } else if (board[x-1][y+1] == 3){
                    moveset.flagCapture.add(new Integer[]{x, y, x-1, y+1});
                }
            }
        }
        if (x != board.length-1){
            if(y != 0){
                // try to capture x+1 , y-1
                if(board[x+1][y-1] == 2){
                    moveset.capture.add(new Integer[]{x, y, x+1, y-1});
                } else if (board[x+1][y-1] == 3){
                    moveset.flagCapture.add(new Integer[]{x, y, x+1, y-1});
                }
            }
            if(y != board.length-1){
                // try to capture x+1 , y+1
                if(board[x+1][y+1] == 2){
                    moveset.capture.add(new Integer[]{x, y, x+1, y+1});
                } else if (board[x+1][y+1] == 3){
                    moveset.flagCapture.add(new Integer[]{x, y, x+1, y+1});
                }
            }
        }
    }

    // Checks if the FLAGSHIP is left in a position that can be captured
    public boolean IsFlagSafe(int[] coor) {
        // Row index of Tile
        int x = coor[0];
        // Column index of Tile
        int y = coor[1];

        if (x != 0){
            if(y != 0){
                // try to capture x-1 , y-1
                if(board[x-1][y-1] == 1){
                    return false;
                }
            }
            if(y != board.length-1){
                // try to capture x-1 , y+1
                if(board[x-1][y+1] == 1){
                    return false;
                }
            }
        }
        if (x != board.length-1){
            if(y != 0){
                // try to capture x+1 , y-1
                if(board[x+1][y-1] == 1){
                    return false;
                }
            }
            if(y != board.length-1){
                // try to capture x+1 , y+1
                if(board[x+1][y+1] == 1){
                    return false;
                }
            }
        }
        return true;
    }

    // RETURN == 0: Not terminal, 1: Gold wins, 2: Silver wins
    public int isTerminalState() {
        int i = flagshipCoor[0];
        int j = flagshipCoor[1];
        int len = board.length;

        // Flagship DED
        if(flagshipCoor.length == 0){return 2;}
        // Flagship escaped
        if (i == 0 || i == len - 1 || j == 0 || j == len - 1) {
            return 1;
        } else {    // Flagship still in play
            return 0;
        }
    }

}
