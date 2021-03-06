package com.company;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchTree {
    State root;
    int depthR;
    public static boolean AIplaysSilver = false;
    //ArrayList<Integer[]> path;  // Dynamic update

    //-------EVALUATION FUNCTION WEIGHTS-------------
    public static int goldFlagMoveValue = 3; //3
    public static int goldShipValue = -10;  //-10
    public static int silverShipValue = 10; //10
    public static int checkingFlagValue = -200; //-200
    //public static int silverFreedomValue = -1;  //null
    //-----------------------------------------------

    public SearchTree(State rootNode, int depth) {
        root = rootNode;
        depthR = depth;
    }

    public static void setSilverShipValue(int value){
        silverShipValue = value;
    }
    public static void setGoldShipValue(int value){
        goldShipValue = value;
    }
    public static void setGoldFlagMoveValue(int value){
        goldFlagMoveValue = value;
    }
    public static void setCheckingFlagValue(int value){
        checkingFlagValue = value;
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

    // Minimax algorithm with ALPHA-BETA pruning
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
                if(child.stateType.equals("SILVER1")){newDepth--;}
                //newDepth--;
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
                if(child.stateType.equals("GOLD1")){newDepth--;}
                //newDepth--;
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
    // P.S This is a cheating method that replicates minmax_alpha_beta but stores the first generation of children nodes.
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
                // ----------CHANGE RULES OF DEPTH HERE----------------------
                // Current setting is to skip depth reduction, when the move is movement of a ship for the first time.
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
                // DEBUG :
                System.out.println("Iterating through child " + counter + " of " + children.toArray().length);
                // ----------CHANGE RULES OF DEPTH HERE----------------------
                // Current setting is to skip depth reduction, when the move is movement of a ship for the first time.
                int newDepth = depth;
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

        //System.out.println("\n\n\n\n\t\t\tFirst chosen move: " + Arrays.toString(firstChosenMove));
        System.out.println("\n\n\n\n\t\t\tFirst chosen move: " + Main.indexToNotation(firstChosenMove));


        if(chosenChild.stateType.equals("SILVER2") || chosenChild.stateType.equals("GOLD2")){
            Integer[] secondChosenMove = chosenChild.findChosenChild().lastMoveMade;
            chosenMove.add(secondChosenMove);
            //System.out.println("\n\t\t\tSecond chosen move: " + Arrays.toString(secondChosenMove));
            System.out.println("\n\t\t\tSecond chosen move: " + Main.indexToNotation(secondChosenMove));
        }
        return chosenMove;
    }

    // P.S This is a SECOND cheating method that replicates minmax_alpha_beta but stores the second generation of children nodes.
    // This is because movement moves require two plies.
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
        int goldWin = 7000;
        int silverWin = -7000;
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

        /*if (AIplaysSilver){
            state.FindSilverandGenerate();
            int silverFreedom = state.moveset.move.size();
            sumValue += silverFreedom * silverFreedomValue;
        }*/

        if(isCheckFlag(state)){
            sumValue += checkingFlagValue;
        }
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

    // Flagship'e destekli bir şekilde şah çeken bir kombinasyon var mı, yok mu?
    public static boolean isCheckFlag(State s){
        int flagX = s.flagshipCoor[0];
        int flagY = s.flagshipCoor[1];
        int[][] board = s.board;

        // FLAG in possible risk of capture
        // I'M GOING TO MAKE IT AN IF ELSE CHAIN BECAUSE IF THERE ARE MULTIPLE SILVER SHIPS CHECKING FLAG, AND ONLY ONE OF THEM HAS SUPPORT
        // THEN IT'S NO DIFFERENT THAN NOT HAVING A SUPPORT AT ALL.
        if (board[flagX-1][flagY-1] == 1){
            if (isSupported(board, flagX-1, flagY-1, 1)){return true;}
        } else if (board[flagX-1][flagY+1] == 1){
            if (isSupported(board, flagX-1, flagY+1, 1)){return true;}
        } else if (board[flagX+1][flagY-1] == 1){
            if (isSupported(board, flagX+1, flagY-1, 1)){return true;}
        } else if (board[flagX+1][flagY+1] == 1){
            if (isSupported(board, flagX+1, flagY+1, 1)){return true;}
        }
        return false;
    }

    // Check if the current ship has an ally
    // x, y --> x-coordinate and y-coordinate of current ship that is checking the flagship
    // ORIGINAL ISSUPPORTED
    /*public static boolean isSupported(int[][] board, int x, int y, int ship){
        int antiShip = ship == 1 ? 2 : 1;
        if (x != 0) {
            if (y != 0) {
                // try to capture x-1 , y-1
                if (board[x - 1][y - 1] == ship) {
                    return true;
                }
            }
            if (y != board.length - 1) {
                // try to capture x-1 , y+1
                if (board[x - 1][y + 1] == ship) {
                    return true;
                }
            }
        }
        if (x != board.length - 1) {
            if (y != 0) {
                // try to capture x+1 , y-1
                if (board[x + 1][y - 1] == ship) {
                    return true;
                }
            }
            if (y != board.length - 1) {
                // try to capture x+1 , y+1
                if (board[x + 1][y + 1] == ship) {
                    return true;
                }
            }
        }
        return false;
    }*/

    // Check if the current ship has an ally
    // x, y --> x-coordinate and y-coordinate of current ship that is checking the flagship
    // EXPERIMENTAL ISSUPPORTED
    public static boolean isSupported(int[][] board, int x, int y, int ship){
        int antiShip = ship == 1 ? 2 : 1;
        if (x != 0) {
            if (y != 0) {
                // try to capture x-1 , y-1
                if (board[x - 1][y - 1] == ship) {
                    return true;
                } else if (board[x - 1][y - 1] == antiShip) {
                    return false;
                }
            }
            if (y != board.length - 1) {
                // try to capture x-1 , y+1
                if (board[x - 1][y + 1] == ship) {
                    return true;
                } else if (board[x - 1][y + 1] == antiShip) {
                    return false;
                }
            }
        }
        if (x != board.length - 1) {
            if (y != 0) {
                // try to capture x+1 , y-1
                if (board[x + 1][y - 1] == ship) {
                    return true;
                } else if (board[x + 1][y - 1] == antiShip) {
                    return false;
                }
            }
            if (y != board.length - 1) {
                    // try to capture x+1 , y+1
                if (board[x + 1][y + 1] == ship) {
                    return true;
                } else if (board[x + 1][y + 1] == antiShip) {
                    return false;
                }
            }
        }
        return false;
    }

}
