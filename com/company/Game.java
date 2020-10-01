package com.company;

import com.company.State;
import com.company.Main;

import java.util.ArrayList;

public class Game extends State{
    public Game(int[][] map, String type) {
        super(map, type);


    }

    public void play(ArrayList<Integer[]> move){
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
        
        // Should the game end here, and the program be terminated?
        gameEnd();
    }

    public void gameEnd(){
        if (this.isTerminalState() != 0){
            // END THE GAME
            Main.GUI(this.board);
            System.out.println("\nGood game.\n");
            System.exit(1);
        }
    }

    /*current board state
    executing moves
    outputting new state of the game
            CHECK IF TERMINAL!!
            end if terminal --> doesn't translate to Search Tree
            ----->>> I need a search tree class*/


}
