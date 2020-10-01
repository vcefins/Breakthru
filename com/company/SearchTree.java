package com.company;
import com.company.State;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SearchTree {
    // Holds root state
    // minimax algorithm
    // alpha beta pruning

    State root;
    int depthR;
    //ArrayList<Integer[]> path;  // Dynamic update

    public SearchTree(State rootNode, int depth) {
        root = rootNode;
        depthR = depth;
    }

    // MINIMAX Algorithm - update stateValue values (Returns: final score of most fitting leaf node)
    public static int minimax(State state, int depth) {
        if (depth <= 0 || state.isTerminalState() != 0) {
            int eval = stateEvaluation(state);
            System.out.println("[" + state.stateType +"-" + depth + "] A state value CREATED in depth " + depth + " : " + eval);
            //return stateEvaluation(state);
            return eval;
        }
        // Score in current node that is used in child values comparison
        int score = 0;

        // MAXIMIZE THE VALUE
        if (state.stateType.equals("GOLD1") || state.stateType.equals("GOLD2")) {
            score = -8000;
            // Find all moves [x,y, x_new, y_new]
            state.FindGoldandGenerate();
            // Get new states and pass them to recurse
            ArrayList<State> children = state.prepareChildrenStatesforTree();

            for(State child : children){
                // ---------CHANGE RULES OF DEPTH-------------------
                // IF current state is G1, children can only be G2 or S1, so if child.G2 --> depth untouched
                // ELSE no problem (all S1)
                int newDepth = depth;
                if(child.stateType.equals("SILVER1")){newDepth--;}
                //---
                int value = minimax(child,newDepth);
                if(value > score){score = value;}
            }
        }

        // MINIMIZE THE VALUE
        else if (state.stateType.equals("SILVER1") || state.stateType.equals("SILVER2")) {
            score = 8000;
            // Find all moves [x,y, x_new, y_new]
            state.FindSilverandGenerate();
            // Get new states and pass them to recurse
            ArrayList<State> children = state.prepareChildrenStatesforTree();

            for(State child : children){
                // -------CHANGE RULES OF DEPTH HERE---------
                int newDepth = depth;
                // If child's type is 'SILVER2' then the movement action is still ongoing, so no reduction in depth
                if(child.stateType.equals("GOLD1")){newDepth--;}
                //------
                int value = minimax(child,newDepth);
                if(value < score){score = value;}
            }
        }

        state.stateValue = score;
        System.out.println("[" + state.stateType +"-" + depth + "] A state value UPDATED in depth " + depth + " : " + score);
        return score;
    }

    // Uses SearchTree.root as root node to generate a search tree, then return the fittest move root can do.
    // P.S Return can be TWO MOVES if it's a movement action {[x,y,z,t], [x,y,z,t]}
    public ArrayList<Integer[]> doMinimaxAndReturnBestMove(int depth){
        //root.children

        // ONE MINIMAX LOOP - Because we need the state itself, not an integer value the original method returns.
        //-----------------
        // Score for child values' comparison
        int score = 0;

        // MAXIMIZE THE VALUE
        if (root.stateType.equals("GOLD1") || root.stateType.equals("GOLD2")) {
            score = -8000;
            // Find all moves [x,y, x_new, y_new]
            root.FindGoldandGenerate();
            // Get new states and pass them to recurse
            ArrayList<State> children = root.prepareChildrenStatesforTree();

            for(State child : children){
                // CHANGE RULES OF DEPTH HERE
                int newDepth = depth;
                if(child.stateType.equals("SILVER1")){newDepth--;}
                //
                int value = minimax(child,newDepth);
                if(value > score){
                    score = value;
                    root.children.add(child);
                }

                // Iteratively adds all children from first generation of moves, so that the move(s) that lead
            }

        }

        // MINIMIZE THE VALUE
        else if (root.stateType.equals("SILVER1") || root.stateType.equals("SILVER2")) {
            score = 8000;
            // Find all moves [x,y, x_new, y_new]
            root.FindSilverandGenerate();
            // Get new states and pass them to recurse
            ArrayList<State> children = root.prepareChildrenStatesforTree();

            for(State child : children){
                // CHANGE RULES OF DEPTH HERE
                int newDepth = depth;
                if(child.stateType.equals("GOLD1")){newDepth--;}
                //------
                int value = minimax(child,newDepth);
                if(value < score){
                    score = value;
                    root.children.add(child);
                }
            }
        }
        root.stateValue = score;    // Whichever child has a matching value is the CHOSEN MOVE
        //----------------END OF MINIMAX REPEAT-------------
        //-----Find the chosed child(ren) and get the optimal moves-----------
        ArrayList<Integer[]> chosenMove = null;
        State chosenChild = root.findChosenChild();
        Integer[] firstChosenMove = chosenChild.lastMoveMade;
        chosenMove.add(firstChosenMove);

        if(chosenChild.stateType.equals("SILVER2") || chosenChild.stateType.equals("GOLD2")){
            Integer[] secondChosenMove = chosenChild.findChosenChild().lastMoveMade;
            chosenMove.add(secondChosenMove);
        }
        return chosenMove;
    }

    // Minimax algorithm with alpha-Beta prunings
    public static int minmax_alpha_beta(State state, int depth, int alpha, int beta) {
        if (depth <= 0.0 || state.isTerminalState() != 0) {     // Leaf Node
            return stateEvaluation(state);
        }
        int score = 0;

        // MAXIMIZE THE VALUE
        if (state.stateType.equals("GOLD1") || state.stateType.equals("GOLD2")) {
            score = -8000;
            // Find all moves [x,y, x_new, y_new]
            state.FindGoldandGenerate();

            // Get new states and pass them to recurse
            ArrayList<State> children = state.prepareChildrenStatesforTree();

            for(State child : children){
                // CHANGE RULES OF DEPTH HERE
                int newDepth = depth;
                //if(child.stateType.equals("SILVER1")){newDepth--;}
                newDepth--;
                //
                int value = minmax_alpha_beta(child, newDepth, alpha, beta);
                alpha = alpha > value ? alpha : value;
                if ( value > score ){ score = value;
                    //System.out.println(alpha);
                }
                if (beta <= alpha){
                    //System.out.println("Pruned");
                    break;}
            }
        }

        // MINIMIZE THE VALUE
        else if (state.stateType.equals("SILVER1") || state.stateType.equals("SILVER2")) {
            score = 8000;
            // Find all moves [x,y, x_new, y_new]
            state.FindSilverandGenerate();

            // Get new states and pass them to recurse
            ArrayList<State> children = state.prepareChildrenStatesforTree();

            for(State child : children){
                // CHANGE RULES OF DEPTH HERE
                int newDepth = depth;
                //if(child.stateType.equals("SILVER1")){newDepth--;}
                newDepth--;
                //
                int value = minmax_alpha_beta(child, newDepth, alpha, beta);
                beta = beta < value ? beta : value;
                if ( value < score ){ score = value;
                    //System.out.println(alpha);
                }
                if (beta <= alpha){
                    //System.out.println("Pruned");
                    break;}
            }
        }
        return score;
    }

    // Uses SearchTree.root as root node to generate a search tree, then return the fittest move root can do.
    public ArrayList<Integer[]> doAlphaBetaAndReturnBestMove(int depth){
        // This is a fake iteration in the recursive loop to command only the first generation of moves to get stored in root.children.
        int rootAlpha = -8000;
        int rootBeta = 8000;

        int score = 0;

        // MAXIMIZE THE VALUE
        if (root.stateType.equals("GOLD1") || root.stateType.equals("GOLD2")) {
            score = -8000;
            // Find all moves [x_current, y_current, x_new, y_new]
            root.FindGoldandGenerate();

            // Get new states and pass them to recurse
            ArrayList<State> children = root.prepareChildrenStatesforTree();

            int counter = 1;
            for(State child : children){
                // DEBUG :
                System.out.println("Iterating through child " + counter + " of " + children.toArray().length);
                // CHANGE RULES OF DEPTH HERE
                int newDepth = depth;
                if(child.stateType.equals("SILVER1")){newDepth--;}
                //newDepth--;
                //
                int value = andAnotherOne(child, newDepth, rootAlpha, rootBeta);
                if ( value > score ){
                    score = value;

                    child.stateValue = score;
                    root.children.add(child);   // Selective child node storing -- only the states with significant values are saved
                }
                //System.out.println("rootAlpha: " + rootAlpha + " trialing against current max value: " + value);
                rootAlpha = rootAlpha > value ? rootAlpha : value;

                //DEBUG
                counter++;
                if (rootBeta <= rootAlpha){break;}
            }
        }

        // MINIMIZE THE VALUE
        else if (root.stateType.equals("SILVER1") || root.stateType.equals("SILVER2")) {
            score = 8000;
            // Find all moves [x,y, x_new, y_new]
            root.FindSilverandGenerate();

            // Get new states and pass them to recurse
            ArrayList<State> children = root.prepareChildrenStatesforTree();

            int counter = 1;
            for(State child : children){
                // CHANGE RULES OF DEPTH HERE
                int newDepth = depth;
                // DEBUG :
                System.out.println("Iterating through child " + counter + " of " + children.toArray().length);
                if(child.stateType.equals("SILVER1")){newDepth--;}
                //newDepth--;
                //
                int value = andAnotherOne(child, newDepth, rootAlpha, rootBeta);
                if ( value < score ){
                    score = value;

                    child.stateValue = score;
                    root.children.add(child);   // Selective child node storing -- only the states with significant values are saved
                }
                rootBeta = rootBeta < value ? rootBeta : value;

                counter++;
                if (rootBeta <= rootAlpha){break;}
            }
        }

        // Assign fittest value among children to root value.
        root.stateValue = score;

        //-----Find the chosed child(ren) and get the optimal moves-----------
        ArrayList<Integer[]> chosenMove = new ArrayList<>();
        State chosenChild = root.findChosenChild();
        Integer[] firstChosenMove = chosenChild.lastMoveMade;
        chosenMove.add(firstChosenMove);

        System.out.println("\n\n\n\n\t\t\tFirst chosen move: " + Arrays.toString(firstChosenMove));

        if(chosenChild.stateType.equals("SILVER2") || chosenChild.stateType.equals("GOLD2")){
            Integer[] secondChosenMove = chosenChild.findChosenChild().lastMoveMade;
            chosenMove.add(secondChosenMove);
            System.out.println("\n\t\t\tSecond chosen move: " + Arrays.toString(secondChosenMove));
        }
        return chosenMove;
    }

    public int andAnotherOne(State state, int depth, int alpha, int beta) {
        // This is a DOUBLE fake iteration in the recursive loop to command only the first generation of moves AND THE SECONDARY SHIP MOVE to get stored in root.children.
        if (depth <= 0.0 || state.isTerminalState() != 0) {     // Leaf Node
            return stateEvaluation(state);
        }
        int score = 0;

        // MAXIMIZE THE VALUE
        if (state.stateType.equals("GOLD1") || state.stateType.equals("GOLD2")) {
            score = -8000;
            // Find all moves [x,y, x_new, y_new]
            state.FindGoldandGenerate();

            // Get new states and pass them to recurse
            ArrayList<State> children = state.prepareChildrenStatesforTree();

            for (State child : children) {
                // CHANGE RULES OF DEPTH HERE
                int newDepth = depth;
                if (child.stateType.equals("SILVER1")) {
                    newDepth--;
                }
                //newDepth--;
                //
                int value = minmax_alpha_beta(child, newDepth, alpha, beta);
                if (value > score) {
                    score = value;

                    child.stateValue = score;
                    state.children.add(child);   // Selective child node storing -- only the states with significant values are saved
                }
                alpha = alpha > value ? alpha : value;

                if (beta <= alpha) {
                    break;
                }
            }
        }

        // MINIMIZE THE VALUE
        else if (state.stateType.equals("SILVER1") || state.stateType.equals("SILVER2")) {
            score = 8000;
            // Find all moves [x,y, x_new, y_new]
            state.FindSilverandGenerate();

            // Get new states and pass them to recurse
            ArrayList<State> children = state.prepareChildrenStatesforTree();

            for (State child : children) {
                // CHANGE RULES OF DEPTH HERE
                int newDepth = depth;
                if (child.stateType.equals("GOLD1")) {
                    newDepth--;
                }
                //newDepth--;
                //
                int value = minmax_alpha_beta(child, newDepth, alpha, beta);
                if (value < score) {
                    score = value;

                    child.stateValue = score;
                    state.children.add(child);   // Selective child node storing -- only the states with significant values are saved
                }
                beta = beta < value ? beta : value;
                if (beta <= alpha) {
                    break;
                }
            }
        }
        state.stateValue = score;
        return score;
    }

    // EVALUATION
    // FUNCTION
    public static int stateEvaluation(State state) {
        int sumValue = 0;

        // Evaluation features
        int goldWin = 5000;
        int silverWin = -5000;
        //--------------------

        if (state.isTerminalState() == 1){
            return goldWin;
        } else if (state.isTerminalState() == 2){
            return silverWin;
        }

        // Get number of ships for both sides
        int[] shipNum = state.countShips();
        int goldCurrentShipNum = shipNum[0];
        int silverCurrentShipNum = shipNum[1];

        // Evaluation features
        int goldTotalShipNum = 13;
        int silverTotalShipNum = 20;

        state.countShips();
        state.GenerateMovementGold(state.flagshipCoor, 3);  // This generates flagship moves and stores in state.moveset.flagship

        int goldFlagMovesNum = state.getNumberOfFlagshipMoves();

        int goldFlagMoveValue = 10;
        int goldShipValue = -11;
        int silverShipValue = 7;

        sumValue += goldFlagMovesNum * goldFlagMoveValue;
        sumValue += goldShipValue * (goldTotalShipNum - goldCurrentShipNum);
        sumValue += silverShipValue * (silverTotalShipNum - silverCurrentShipNum);
        //--------------------

        // DEBUG
        /*Main.GUI(state.board);
        System.out.println("State Type: " + state.stateType);
        System.out.println("Number of gold flagship moves: " + goldFlagMovesNum);
        System.out.println("Evaluation Product: " + sumValue + "\n\n\n");*/


        return sumValue;
    }

}
