package com.company;

import com.company.State;

import javax.swing.*;
import java.util.ArrayList;

public class SearchTree {
    // Holds root state
    // minimax algorithm
    // alpha beta pruning

    State root;
    ArrayList<Integer[]> path;  // Dynamic update

    public SearchTree(State rootNode) {
        root = rootNode;
    }

    public static int minimax(State state, int depth, String type) {
        if (depth <= 0 || state.isTerminalState() != 0) {
            return stateEvaluation(state);
        }
        int score = 0;

        // MAXIMIZE THE VALUE
        if (type.equals("GOLD1") || type.equals("GOLD2")) {
            int tempMax = 8000;
            // Find all moves [x,y, x_new, y_new]
            state.FindGoldandGenerate();
            // Get new states and pass them to recurse
            ArrayList<State> children = state.prepareChildrenStatesforTree();

            for(State child : children){
                // CHANGE RULES OF DEPTH HERE
                int newDepth = depth - 1;
                //
                int value = minimax(child,depth-1, child.stateType);
                if(value > score){score = value;}
            }
        }

        // MINIMIZE THE VALUE
        if (type.equals("SILVER1") || type.equals("SILVER2")) {
            int tempMin = -8000;
            // Find all moves [x,y, x_new, y_new]
            state.FindSilverandGenerate();
            // Get new states and pass them to recurse
            ArrayList<State> children = state.prepareChildrenStatesforTree();

            for(State child : children){
                // CHANGE RULES OF DEPTH HERE
                int newDepth = depth - 1;
                //------
                int value = minimax(child,depth-1, child.stateType);
                if(value < score){score = value;}
            }
        }
        return score;
    }


    public static int minmax_alpha_beta(State state, int depth, int alpha, int beta, String type) {
        if (depth <= 0.0 || state.isTerminalState() != 0) {
            return stateEvaluation(state);
        }
        int score = 0;

        // MAXIMIZE THE VALUE
        if (type.equals("GOLD1") || type.equals("GOLD2")) {
            int tempMax = 8000;
            // Find all moves [x,y, x_new, y_new]
            state.FindGoldandGenerate();

            // Get new states and pass them to recurse
            ArrayList<State> children = state.prepareChildrenStatesforTree();

            for(State child : children){
                // CHANGE RULES OF DEPTH HERE
                int newDepth = depth - 1;
                //
                int value = minmax_alpha_beta(child,depth-1, alpha, beta, child.stateType);
            }
        }

        // MINIMIZE THE VALUE
        if (type.equals("SILVER1") || type.equals("SILVER2")) {
            int tempMin = -8000;
            // Find all moves [x,y, x_new, y_new]
            state.FindSilverandGenerate();

            // Get new states and pass them to recurse
            state.prepareChildrenStatesforTree();
        }
        return 0;       //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
    }


    // EVALUATION
    // FUNCTION
    public static int stateEvaluation(State state) {
        int sumValue = 0;

        // Evaluation features
        int goldWin = 500;
        int silverWin = -500;
        //--------------------

        if (state.isTerminalState() == 1){
            return goldWin;
        } else if (state.isTerminalState() == 2){
            return silverWin;
        }

        // Evaluation features
        int goldTotalShipNum = 13;
        int silverTotalShipNum = 20;
        int goldCurrentShipNum = state.goldShipCurrentNum;
        int silverCurrentShipNum = state.silverShipCurrentNum;

        int goldLoseShipPoints = -10;
        int silverLoseShipPoints = 10;

        sumValue += goldLoseShipPoints * (goldTotalShipNum - goldCurrentShipNum);
        sumValue += silverLoseShipPoints * (silverTotalShipNum - silverCurrentShipNum);
        //--------------------

        return sumValue;
    }



}
