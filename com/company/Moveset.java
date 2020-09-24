package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


// This is a transport class for move set data, storing capture and movement moves
// An example of a move element in a list : [current x coordinate, current y, next x coordinate, next y]
public class Moveset {
    public List<Integer[]> capture = new ArrayList<Integer[]>();
    public List<Integer[]> move = new ArrayList<Integer[]>();
    public List<Integer[]> flagship = new ArrayList<Integer[]>();       // Only for GOLD
    public List<Integer[]> flagCapture = new ArrayList<Integer[]>();    // Only for SILVER

    public Moveset(){
    }

    public void printMoveset(){
        System.out.println("Capture Moves");
        move.forEach(move -> System.out.println(Arrays.toString(move)));
        System.out.println("Movement Moves");
        move.forEach(move -> System.out.println(Arrays.toString(move)));
        System.out.println("Flagship Moves (if applicable)");
        move.forEach(move -> System.out.println(Arrays.toString(move)));


    }

    /*public Moveset(boolean flagship=false){
        if (flagship == true)
    }*/
}
