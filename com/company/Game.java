package com.company;

import com.company.State;
import com.company.Main;

import java.util.Arrays;
import java.util.Scanner;

import java.util.ArrayList;

public class Game extends State{
    int round = 0;                  // IF ROUND > 20 OR 30 --> SWITCH THE EVALUATION FUNCTION
    ArrayList<Integer[]> gameLog = new ArrayList<>();

    public Game(int[][] map, String type) {
        super(map, type);
    }

    public void play(ArrayList<Integer[]> move){
        // IF there is one element in list
        Integer[] firstM = move.get(0);

        int tempShip = board[firstM[0]][firstM[1]];
        board[firstM[0]][firstM[1]] = 0;
        board[firstM[2]][firstM[3]] = tempShip;

        // Add move to log
        gameLog.add(firstM);

        // IF there is a second element in list
        if (move.toArray().length == 2){
            Integer[] secondM = move.get(1);
            tempShip = board[secondM[0]][secondM[1]];
            board[secondM[0]][secondM[1]] = 0;
            board[secondM[2]][secondM[3]] = tempShip;

            // Add move to log
            gameLog.add(secondM);
        }

        round++;
        // Should the game end here, and the program be terminated?
        gameEnd();
    }

    public void gameEnd(){
        if (this.isTerminalState() != 0){
            // END THE GAME
            Main.GUI(this.board);
            System.out.println("\nGood game.\n");
            System.out.println("\nDo you want the game log? (Y/N)");
            Scanner scan = new Scanner(System.in);
            if(scan.nextLine().equals("Y")){
                for(Integer[] array : gameLog) {
                    System.out.println(Main.indexToNotation(array));
                }
            }
            System.exit(1);
        }
    }

    // DEBUG
    public void playTest(ArrayList<Integer[]> move){
        // IF there is one element in list
        Integer[] firstM = move.get(0);
        int tempShip = board[firstM[0]][firstM[1]];
        board[firstM[0]][firstM[1]] = 0;
        board[firstM[2]][firstM[3]] = tempShip;

        // IF there is a second element in list
        if (move.toArray().length == 2){
            Integer[] secondM = move.get(1);
            tempShip = board[secondM[0]][secondM[1]];
            board[secondM[0]][secondM[1]] = 0;
            board[secondM[2]][secondM[3]] = tempShip;
        }

        round++;
        // Should the game end here, and the program be terminated?
    }

    public int gameEndTest(){
        if (this.isTerminalState() != 0){
            // END THE GAME
            Main.GUI(this.board);
            System.out.println("\nGood game.\n");
            //System.exit(1);
            return this.isTerminalState();
        }
        return 0;
    }

    /*current board state
    executing moves
    outputting new state of the game
            CHECK IF TERMINAL!!
            end if terminal --> doesn't translate to Search Tree
            ----->>> I need a search tree class*/


}
