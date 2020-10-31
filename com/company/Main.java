package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args){

        // ----GAME SETTINGS---------------------------------------//
        // If you want the AI to play GOLD, set isSideGold to TRUE; if you want the AI to play SILVER, set isSideGold to FALSE
        boolean isSideGold = true;
        // Implementing selection of human's side to prepare for executable file format
        boolean flag = true;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which side do you want to play as against the mighty AI? (Gold/Silver)");
        while(flag){
            String humanSide = scanner.nextLine();
            if (humanSide.equals("Gold")){
                isSideGold = false;
                flag = false;
            } else if (humanSide.equals("Silver")){
                flag = false;
            } else {
                System.out.println("You have entered an inadmissible input. Please try again.\n");
            }
        }
        String type = isSideType(isSideGold);    // set initial type
        // Determine SEARCH DEPTH (This is overriden in humanVsAi for tournament (see: HACK)
        int depth = 2;
        //---------------------------------------------------------//

        // -----GAME SETUP-----------------------------------------//
        int[][] map = new int[11][11];
        ShipSetup(map);
        //Manually reconfigure board (for some reason this doesn't work when inside ShipSetup method.)
        /*map = new int[][]{{0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 2, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 2, 1, 0, 0, 2, 0, 0},
                {0, 0, 1, 2, 0, 2, 1, 2, 0, 0, 0},
                {0, 1, 2, 0, 3, 1, 0, 0, 0, 0, 0},
                {0, 0, 1, 2, 1, 2, 0, 2, 0, 0, 1},
                {0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };*/

        Game gameState = new Game(map, type);
        SearchTree2 searchTree = new SearchTree2(gameState, gameState, depth, !isSideGold);
        //---------------------------------------------------------//
        humanVsAi(gameState, isSideGold, searchTree, depth);

        //-----GAME MODE-------------------------------------------//
        // Select a Java method by uncommenting the respective code.
        /*System.out.println("1: Selfplay\n2: Human vs AI\n3: Compete two depths\n4: Compete current and previous evaluation functions");
        switch(new Scanner(System.in).nextInt()) {
            // CASE 1 : Infinite self-play loop among the most recent version of the system.
            case 1 : selfplayAlphaBeta(gameState, searchTree, depth);
                break;

            //CASE 2 : Test yourself against the artificial intelligence agent.
            case 2 : humanVsAi(gameState, isSideGold, searchTree, depth);
                break;

            // CASE 3 : Test which depth is better.
            case 3 : selfplayABTwoDepths(map, isSideGold, 3, 2);
                break;

            // CASE 4 : Test different evaluation functions against each other.
            case 4 : selfplayAlphaBetaDifferentEval(gameState, isSideGold, depth);
                break;

            //CASE 5 : Manual testing for artificial intelligence agent.
            case 5 : humanVsAiForTesting(gameState, isSideGold, searchTree, depth);
                break;

        }*/
        //DEBUG
        //playOnePlyDeepGame(map, type);
        //currentState.moveset.move.forEach(move -> System.out.println(Arrays.toString(move)));
        //GenerateMovesforTile(new int[]{1, 3}, map);
    }

    // AI plays a game versus current version
    public static void selfplayAlphaBeta(Game gameState, SearchTree2 searchTree, int depth){
        GUI(gameState.board);
        boolean isSideGold = true;
        while(true) {
            // ---Timer start---
            long startTime = System.currentTimeMillis();

            // DEBUG : Playout
            //gameState.play(searchTree.doMinimaxAndReturnBestMove(depth));
            //int tempo = SearchTree.minmax_alpha_beta(searchTree.root, depth, -8000, 8000);

            ArrayList<Integer[]> chosenMoves = searchTree.doAlphaBetaAndReturnBestMove(depth);
            System.out.println("\nAlpha-Beta search completed.");

            // ---Timer end---
            long endTime = System.currentTimeMillis();
            long timeElapsed = endTime - startTime;
            System.out.println("Execution time in seconds: " + (double) timeElapsed / 1000);
            //-----------------

            // Play the optimal moves
            gameState.play(chosenMoves);

            // CIRCULATE TYPE and RE-ROOT
            isSideGold = !isSideGold;
            State newRoot = new State(gameState.board, isSideType(isSideGold));
            searchTree.AIplaysSilver = isSideGold;
            searchTree.root = newRoot;

            // Visualize
            GUI(gameState.board);
        }
    }

    // Human versus AI's current version
    // HACK applied (switch between depth 2 and 3)
    public static void humanVsAi(Game gameState, boolean isSideGold, SearchTree2 searchTree, int depth){
        GUI(gameState.board);
        long totalPlayTime = 0;
        while(true) {

            // IF isSideGold = false, player goes first
            if(!isSideGold){
                String side = isSideGold ? "Silver" : "Gold" ;
                System.out.println("\nEnter your next move " + side + " fleet captain!\t(Enter a move strictly as such: a1->a2)");
                Scanner scanner = new Scanner(System.in);

                // Failsafe to reset input if input is wrong
                boolean flag2 = true;
                while (flag2){
                    Integer[] humanFirstMove;
                    try {
                        humanFirstMove = notationToIndex(scanner.nextLine());
                    } catch (Exception e){
                        System.out.println("\nSorry but you entered an illegal or bogus move. Try again.");
                        continue;
                    }
                    ArrayList<Integer[]> humanMove = new ArrayList<Integer[]>();
                    //IF this is a capture move, play the move : ELSE scan for the next move.
                    switch (humanMoveLegal(humanFirstMove, gameState.board, !isSideGold)) {
                        case 0:
                            System.out.println("\nSorry but you entered an illegal or bogus move. Try again.");
                            break;
                        case 1:
                            humanMove.add(humanFirstMove);
                            System.out.println("Enter the second ship to sail!");
                            Integer[] humanSecondMove = notationToIndex(scanner.nextLine());
                            // DEBUG legal move check doesn't work for the second move entered if it has common coordinates with the first move
                            if (true || humanMoveLegal(humanSecondMove, gameState.board, !isSideGold) != 0) {
                                humanMove.add(humanSecondMove);
                            }
                            gameState.play(humanMove);
                            flag2 = false;
                            break;
                        case 2:
                            humanMove.add(humanFirstMove);
                            gameState.play(humanMove);
                            flag2 = false;
                            break;
                    }
                }
                GUI(gameState.board);
            }

            // ---Timer start---
            long startTime = System.currentTimeMillis();

            // DEBUG : Playout
            //gameState.play(searchTree.doMinimaxAndReturnBestMove(depth));
            //int tempo = SearchTree.minmax_alpha_beta(searchTree.root, depth, -8000, 8000);

            int effectiveDepth = depth;
            // HACK: Switching between depth 2 and 3 for time efficiency in tournament
            if(isSideGold){
                if (gameState.round % 6 == 4){
                    effectiveDepth = 3;
                } else {
                    effectiveDepth = 2;
                }
            } else {
                effectiveDepth = 2;
            }

            ArrayList<Integer[]> chosenMoves = searchTree.doAlphaBetaAndReturnBestMove(effectiveDepth);

            //ArrayList<Integer[]> chosenMoves = searchTree.doAlphaBetaAndReturnBestMove(depth);
            //System.out.println("\nAlpha-Beta search completed. ");

            // ---Timer end---
            long endTime = System.currentTimeMillis();
            long timeElapsed = endTime - startTime;
            totalPlayTime += timeElapsed;
            System.out.println("Execution time in seconds: " + (double) timeElapsed / 1000);
            System.out.println("Total play time in seconds: " + (double) totalPlayTime / 1000);
            System.out.println("Average move time in seconds: " + (double) totalPlayTime / ((gameState.round/2 + 1) * 1000) + "\n\n");
            //-----------------

            // Play the optimal moves
            gameState.play(chosenMoves);
            searchTree.root = new State(gameState.board, isSideType(isSideGold));

            // Visualize
            GUI(gameState.board);

            // IF isSideGold = true, player goes second
            if(isSideGold){
                String side = isSideGold ? "Silver" : "Gold" ;
                System.out.println("\nEnter your next move " + side + " fleet captain!\t(Enter a move strictly as such: a1->a2)");
                Scanner scanner = new Scanner(System.in);

                // Failsafe to reset input if input is wrong
                boolean flag2 = true;
                while (flag2){
                    Integer[] humanFirstMove;
                    try {
                        humanFirstMove = notationToIndex(scanner.nextLine());
                    } catch (Exception e){
                        System.out.println("\nSorry but you entered an illegal or bogus move. Try again.");
                        continue;
                    }
                    ArrayList<Integer[]> humanMove = new ArrayList<Integer[]>();
                    //IF this is a capture move, play the move : ELSE scan for the next move.
                    switch (humanMoveLegal(humanFirstMove, gameState.board, !isSideGold)) {
                        case 0:
                            System.out.println("\nSorry but you entered an illegal or bogus move. Try again.");
                            break;
                        case 1:
                            humanMove.add(humanFirstMove);
                            System.out.println("Enter the second ship to sail!");
                            Integer[] humanSecondMove = notationToIndex(scanner.nextLine());
                            // DEBUG legal move check doesn't work for the second move entered if it has common coordinates with the first move
                            if (true || humanMoveLegal(humanSecondMove, gameState.board, !isSideGold) != 0) {
                                humanMove.add(humanSecondMove);
                            }
                            gameState.play(humanMove);
                            flag2 = false;
                            break;
                        case 2:
                            humanMove.add(humanFirstMove);
                            gameState.play(humanMove);
                            flag2 = false;
                            break;
                    }
                }
                GUI(gameState.board);
            }

        }
    }

    // FOR TESTING PURPOSES
    public static void humanVsAiForTesting(Game gameState, boolean isSideGold, SearchTree2 searchTree, int depth){
        GUI(gameState.board);
        long totalPlayTime = 0;
        while(true) {

            // IF isSideGold = false, player goes first
            if(!isSideGold){
                String side = isSideGold ? "Silver" : "Gold" ;
                System.out.println("\nEnter your next move " + side + " fleet captain!\t(Enter a move strictly as such: a1->a2)");
                Scanner scanner = new Scanner(System.in);

                // Failsafe to reset input if input is wrong
                boolean flag2 = true;
                while (flag2){
                    Integer[] humanFirstMove;
                    try {
                        humanFirstMove = notationToIndex(scanner.nextLine());
                    } catch (Exception e){
                        System.out.println("\nSorry but you entered an illegal or bogus move. Try again.");
                        continue;
                    }
                    ArrayList<Integer[]> humanMove = new ArrayList<Integer[]>();
                    //IF this is a capture move, play the move : ELSE scan for the next move.
                    switch (humanMoveLegal(humanFirstMove, gameState.board, !isSideGold)) {
                        case 0:
                            System.out.println("\nSorry but you entered an illegal or bogus move. Try again.");
                            break;
                        case 1:
                            humanMove.add(humanFirstMove);
                            System.out.println("Enter the second ship to sail!");
                            Integer[] humanSecondMove = notationToIndex(scanner.nextLine());
                            // DEBUG legal move check doesn't work for the second move entered if it has common coordinates with the first move
                            if (true || humanMoveLegal(humanSecondMove, gameState.board, !isSideGold) != 0) {
                                humanMove.add(humanSecondMove);
                            }
                            gameState.play(humanMove);
                            flag2 = false;
                            break;
                        case 2:
                            humanMove.add(humanFirstMove);
                            gameState.play(humanMove);
                            flag2 = false;
                            break;
                    }
                }
                GUI(gameState.board);
            }

            // DEBUG
            //System.out.println("What is wrong with states? " + gameState.stateType);

            // ---Timer start---
            long startTime = System.currentTimeMillis();

            // DEBUG : Playout
            //gameState.play(searchTree.doMinimaxAndReturnBestMove(depth));
            //int tempo = SearchTree.minmax_alpha_beta(searchTree.root, depth, -8000, 8000);

            int effectiveDepth = depth;
            // HACK: Switching between depth 2 and 3 for time efficiency in tournament

            /*if(isSideGold){
                if (gameState.round % 6 == 4){
                    effectiveDepth = 3;
                } else {
                    effectiveDepth = 2;
                }
            } else {
                effectiveDepth = 2;
            }*/

            ArrayList<Integer[]> chosenMoves = searchTree.doAlphaBetaAndReturnBestMove(effectiveDepth);
            //ArrayList<Integer[]> chosenMoves = searchTree.doAlphaBetaAndReturnBestMove(depth);
            //System.out.println("\nAlpha-Beta search completed. ");

            // FLAT MINIMAX
            //SearchTree2.minimax(searchTree.root, effectiveDepth);

            // ---Timer end---
            long endTime = System.currentTimeMillis();
            long timeElapsed = endTime - startTime;
            totalPlayTime += timeElapsed;
            System.out.println("Execution time in seconds: " + (double) timeElapsed / 1000);
            System.out.println("Total play time in seconds: " + (double) totalPlayTime / 1000);
            System.out.println("Average move time in seconds: " + (double) totalPlayTime / ((gameState.round/2 + 1) * 1000));
            //-----------------

            // Play the optimal moves
            gameState.play(chosenMoves);
            searchTree.root = new State(gameState.board, isSideType(isSideGold));

            // Visualize
            GUI(gameState.board);

            // IF isSideGold = true, player goes second
            if(isSideGold){
                String side = isSideGold ? "Silver" : "Gold" ;
                System.out.println("\nEnter your next move " + side + " fleet captain!\t(Enter a move strictly as such: a1->a2)");
                Scanner scanner = new Scanner(System.in);

                // Failsafe to reset input if input is wrong
                boolean flag2 = true;
                while (flag2){
                    Integer[] humanFirstMove;
                    try {
                        humanFirstMove = notationToIndex(scanner.nextLine());
                    } catch (Exception e){
                        System.out.println("\nSorry but you entered an illegal or bogus move. Try again.");
                        continue;
                    }
                    ArrayList<Integer[]> humanMove = new ArrayList<Integer[]>();
                    //IF this is a capture move, play the move : ELSE scan for the next move.
                    switch (humanMoveLegal(humanFirstMove, gameState.board, !isSideGold)) {
                        case 0:
                            System.out.println("\nSorry but you entered an illegal or bogus move. Try again.");
                            break;
                        case 1:
                            humanMove.add(humanFirstMove);
                            System.out.println("Enter the second ship to sail!");
                            Integer[] humanSecondMove = notationToIndex(scanner.nextLine());
                            // DEBUG legal move check doesn't work for the second move entered if it has common coordinates with the first move
                            if (true || humanMoveLegal(humanSecondMove, gameState.board, !isSideGold) != 0) {
                                humanMove.add(humanSecondMove);
                            }
                            gameState.play(humanMove);
                            flag2 = false;
                            break;
                        case 2:
                            humanMove.add(humanFirstMove);
                            gameState.play(humanMove);
                            flag2 = false;
                            break;
                    }
                }
                GUI(gameState.board);
            }

        }
    }

    // Manually entered move received in index format. CHECK IF move in generated moveset
    // Parameter humanSide: TRUE if human is Gold, FALSE if human is Silver
    // RETURN --> 0 : Illegal move , 1 : Legal Movement Move , 2 : Legal Capture Move OR Legal Flagship Movement //
    private static int humanMoveLegal(Integer[] humanMove, int[][] board, boolean humanSide) {
        try {
            ArrayList<Integer[]> legalMovement = new ArrayList<>();
            ArrayList<Integer[]> legalCapture = new ArrayList<>();
            ArrayList<Integer[]> legalFlag = new ArrayList<>();
            int x = humanMove[0];
            int y = humanMove[1];
            int next_x = humanMove[2];
            int next_y = humanMove[3];

            if (board[x][y] != 1 && !humanSide){
                return 0;
            } else if (board[x][y] == 1 && humanSide){
                return 0;
            }

            if(humanSide){legalFlag.add(new Integer[]{5, 5, 5, 5});}

            // Search for legal movement moves
            // Search WEST
            for (int i = y - 1; i >= 0; i--) {
                if (board[x][i] != 0) {
                    break;
                } else if (humanSide && board[x][y] == 3) {
                    legalFlag.add(new Integer[]{x, y, x, i});
                } else {
                    legalMovement.add(new Integer[]{x, y, x, i});   //Move --> [x, y -> x, i]
                }
            }

            // Search EAST
            for (int i = y + 1; i < board.length; i++) {
                if (board[x][i] != 0) {
                    break;
                } else if (humanSide && board[x][y] == 3) {
                    legalFlag.add(new Integer[]{x, y, x, i});
                } else {
                    legalMovement.add(new Integer[]{x, y, x, i});
                }
            }

            // Search NORTH
            for (int i = x - 1; i >= 0; i--) {
                if (board[i][y] != 0) {
                    break;
                } else if (humanSide && board[x][y] == 3) {
                    legalFlag.add(new Integer[]{x, y, i, y});
                } else {
                    legalMovement.add(new Integer[]{x, y, i, y});
                }
            }

            // Search SOUTH
            for (int i = x + 1; i < board.length; i++) {
                //System.out.println("Now checking " + i + " : " + y);
                if (board[i][y] != 0) {
                    break;
                } else if (humanSide && board[x][y] == 3) {
                    legalFlag.add(new Integer[]{x, y, i, y});
                } else {
                    //System.out.println("Add column: " + i);
                    legalMovement.add(new Integer[]{x, y, i, y});
                }
            }

            // Apparently you have to override equals to use .contains. This will definitely mess other things up. So;
            for (Integer[] move : legalFlag) {     // Return legal movement
                if (Arrays.equals(move, humanMove)) {
                    return 2;
                }
            }

            for (Integer[] move : legalMovement) {     // Return legal movement
                if (Arrays.equals(move, humanMove)) {
                    return 1;
                }
            }

            // Search CAPTURE
            if (x != 0) {
                if (y != 0) {
                    // try to capture x-1 , y-1
                    if (humanSide) {
                        if (board[x - 1][y - 1] == 1) {
                            legalCapture.add(new Integer[]{x, y, x - 1, y - 1});
                        }
                    } else {
                        if (board[x - 1][y - 1] == 2 || board[x - 1][y - 1] == 3) {
                            legalCapture.add(new Integer[]{x, y, x - 1, y - 1});
                        }
                    }
                }
                if (y != board.length - 1) {
                    // try to capture x-1 , y+1
                    if (humanSide) {
                        if (board[x - 1][y + 1] == 1) {
                            legalCapture.add(new Integer[]{x, y, x - 1, y + 1});
                        }
                    } else {
                        if (board[x - 1][y + 1] == 2 || board[x - 1][y + 1] == 3) {
                            legalCapture.add(new Integer[]{x, y, x - 1, y + 1});
                        }
                    }
                }
            }
            if (x != board.length - 1) {
                if (y != 0) {
                    // try to capture x+1 , y-1
                    if (humanSide) {
                        if (board[x + 1][y - 1] == 1) {
                            legalCapture.add(new Integer[]{x, y, x + 1, y - 1});
                        }
                    } else {
                        if (board[x + 1][y - 1] == 2 || board[x + 1][y - 1] == 3) {
                            legalCapture.add(new Integer[]{x, y, x + 1, y - 1});
                        }
                    }
                }
                if (y != board.length - 1) {
                    // try to capture x+1 , y+1
                    if (humanSide) {
                        if (board[x + 1][y + 1] == 1) {
                            legalCapture.add(new Integer[]{x, y, x + 1, y + 1});
                        }
                    } else {
                        if (board[x + 1][y + 1] == 2 || board[x + 1][y + 1] == 3) {
                            legalCapture.add(new Integer[]{x, y, x + 1, y + 1});
                        }
                    }
                }
            }

            for (Integer[] move : legalCapture) {     // Return legal capture
                if (Arrays.equals(move, humanMove)) {
                    return 2;
                }
            }

            // Return illegal move
            return 0;
        } catch (Exception e){
            return 0;
        }
    }

    // Fills the board to starting position (takes empty map array)
    public static void ShipSetup(int[][] map){
        map[5][5] = 3;  //FLAGSHIP
        // Silver
        map[1][3] = map[1][4] = map[1][5] = map[1][6] = map[1][7] = 1;
        map[3][1] = map[4][1] = map[5][1] = map[6][1] = map[7][1] = 1;
        map[3][9] = map[4][9] = map[5][9] = map[6][9] = map[7][9] = 1;
        map[9][3] = map[9][4] = map[9][5] = map[9][6] = map[9][7] = 1;

        //map[9][5] = 0;
        //map[10][4] = 1;
        //Gold
        map[3][4] = map[3][5] = map[3][6] = 2;
        map[4][3] = map[5][3] = map[6][3] = 2;
        map[4][7] = map[5][7] = map[6][7] = 2;
        map[7][4] = map[7][5] = map[7][6] = 2;

        //map[7][5] = 0;
        //map[8][6] = 2;

        /*map = new int[][]{{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
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

        for (int[] ints : map) {
            StringBuilder temp = new StringBuilder();
            for (int anInt : ints) {
                if (anInt != 0) {
                    temp.append("\t").append(anInt);
                } else {
                    temp.append("\t.");
                }
            }
            System.out.println(count + "\t" + temp);
            count--;
        }

        StringBuilder letters = new StringBuilder("\n\t\t");
        for(int i = 0; i < map.length ;i++)
        {
            letters.append(alphabet[i]).append("\t");
        }
        System.out.println(letters);
    }

    // Returns side's respective state type
    public static String isSideType(boolean isGold){
        if (isGold) {return "GOLD1";} else {return "SILVER1";}
    }

    // Convert array indeces to regular chess notation
    public static String indexToNotation(Integer[] move){
        int prevX = move[0];
        int prevY = move[1];
        int nextX = move[2];
        int nextY = move[3];

        char[] alphabet = "abcdefghijk".toCharArray();

        String prevYS = String.valueOf(11 - prevX);
        char prevXS = alphabet[prevY];
        String nextYS = String.valueOf(11 - nextX);;
        char nextXS = alphabet[nextY];


        return prevXS + prevYS + " -> " + nextXS + nextYS;
    }

    // Convert regular chess notation to array indices
    // Fun fact: This method was written with extreme panic during the ISG lab session that trialed participants.
    public static Integer[] notationToIndex(String not){
        // not -> chess notation that looks as such : b4 -> b8
        // String[] tokens = not.split("");
        Integer[] move = new Integer[4];

        // Index of the letter is the x-coordinate
        //char[] alphabet = "abcdefghijk".toCharArray();

        //  PARSE WITH ->
        String[] tokens = not.split("->");
        char initPosY = tokens[0].charAt(0);
        String initPosX = tokens[0].substring(1);
        char nextPosY = tokens[1].charAt(0);
        String nextPosX = tokens[1].substring(1);

        // DEBUG
        // System.out.println("Initial position: " + initPosX + "-" + initPosY + ".");
        // System.out.println("Next position: " + nextPosX + "-" + nextPosY + ".");


        //  tokens[0] --> ship initial location in chess notation
        //String shipInitialLocX = tokens[1];     // X-COORDINATE IS THE NUMBER PART
        //char shipInitialLocY = tokens[0].charAt(0);

        switch (initPosY) {
            case 'a':
                move[1] = 0;
                break;
            case 'b':
                move[1] = 1;
                break;
            case 'c':
                move[1] = 2;
                break;
            case 'd':
                move[1] = 3;
                break;
            case 'e':
                move[1] = 4;
                break;
            case 'f':
                move[1] = 5;
                break;
            case 'g':
                move[1] = 6;
                break;
            case 'h':
                move[1] = 7;
                break;
            case 'i':
                move[1] = 8;
                break;
            case 'j':
                move[1] = 9;
                break;
            case 'k':
                move[1] = 10;
                break;
        }

        // b4
        move[0] = 11 - Integer.parseInt(initPosX);
        //move[1] = Arrays.asList(alphabet).indexOf(shipInitialLocY);

        //  tokens[1] --> ->
        //  tokens[2] --> ship next location in chess notation
        //String shipNextLocX = tokens[7];
        //char shipNextLocY = tokens[6].charAt(0);
        move[2] = 11 - Integer.parseInt(nextPosX);

        switch (nextPosY) {
            case 'a':
                move[3] = 0;
                break;
            case 'b':
                move[3] = 1;
                break;
            case 'c':
                move[3] = 2;
                break;
            case 'd':
                move[3] = 3;
                break;
            case 'e':
                move[3] = 4;
                break;
            case 'f':
                move[3] = 5;
                break;
            case 'g':
                move[3] = 6;
                break;
            case 'h':
                move[3] = 7;
                break;
            case 'i':
                move[3] = 8;
                break;
            case 'j':
                move[3] = 9;
                break;
            case 'k':
                move[3] = 10;
                break;

        }
        //move[3] = Arrays.asList(alphabet).indexOf(shipNextLocY);

        //System.out.println(Arrays.toString(move));

        return move;
    }

    //DEBUG and TESTING//

    // TESTING - AIs with different evaluation functions play against each other
    public static void selfplayAlphaBetaDifferentEval(Game gameState, boolean isSideGold, int depth){
        SearchTree searchTree = new SearchTree(gameState, depth);
        SearchTree2 searchTree2 = new SearchTree2(gameState, gameState, depth, !isSideGold);
        long totalPlayTime = 0;
        long totalPlayTime2 = 0;
        while(true) {
            // ---Timer start---
            long startTime = System.currentTimeMillis();

            ArrayList<Integer[]> chosenMoves = searchTree2.doAlphaBetaAndReturnBestMove(depth);
            //System.out.println("\nAlpha-Beta search completed.");

            // ---Timer end---
            long endTime = System.currentTimeMillis();
            long timeElapsed = endTime - startTime;
            totalPlayTime += timeElapsed;
            System.out.println("Gold execution time in seconds: " + (double) timeElapsed / 1000);
            System.out.println("Gold total play time in seconds: " + (double) totalPlayTime / 1000);
            System.out.println("Gold average move time in seconds: " + (double) totalPlayTime / ((gameState.round/2 + 1) * 1000));
            //-----------------

            // Play the optimal moves
            gameState.play(chosenMoves);
            // Visualize
            GUI(gameState.board);

            searchTree2.root = new State(gameState.board, isSideType(!isSideGold));

            // ---Timer start---
            long startTime2 = System.currentTimeMillis();

            ArrayList<Integer[]> chosenMoves2 = searchTree.doAlphaBetaAndReturnBestMove(depth);
            //ystem.out.println("\nAlpha-Beta search completed.");

            // ---Timer end---
            long endTime2 = System.currentTimeMillis();
            long timeElapsed2 = endTime2 - startTime2;
            totalPlayTime2 += timeElapsed2;
            System.out.println("Execution time in seconds: " + (double) timeElapsed2 / 1000);
            System.out.println("Total play time in seconds: " + (double) totalPlayTime2 / 1000);
            System.out.println("Average move time in seconds: " + (double) totalPlayTime2 / ((gameState.round/2 + 1) * 1000));
            //-----------------

            // Play the optimal moves
            gameState.play(chosenMoves2);

            searchTree.root = new State(gameState.board, isSideType(isSideGold));

            // Visualize
            GUI(gameState.board);
        }
    }

    // AI plays n games versus current version addon
    /*public static int selfplayAlphaBetaTest(int[][] map, String type, boolean isSideGold, int depth){
        // F**k Java
        int[][] newBoardEveryTime = new int[map.length][map.length];
        for (int i = 0; i < map.length; i++){
            for (int j = 0; j < map.length; j++){
                newBoardEveryTime[i][j] = map[i][j];
            }
        }
        Game gamestateClone = new Game(newBoardEveryTime, type);
        SearchTree searchTreeClone = new SearchTree(gamestateClone, depth);


        while(true) {
            // ---Timer start---
            long startTime = System.currentTimeMillis();

            // DEBUG : Playout
            //gameState.play(searchTree.doMinimaxAndReturnBestMove(depth));
            //int tempo = SearchTree.minmax_alpha_beta(searchTree.root, depth, -8000, 8000);

            ArrayList<Integer[]> chosenMoves = searchTreeClone.doAlphaBetaAndReturnBestMove(depth);
            System.out.println("\nAlpha-Beta search completed. ");

            // ---Timer end---
            long endTime = System.currentTimeMillis();
            long timeElapsed = endTime - startTime;
            System.out.println("Execution time in seconds: " + (double) timeElapsed / 1000);
            //-----------------

            // Play the optimal moves
            gamestateClone.playTest(chosenMoves);
            int term = gamestateClone.gameEndTest();
            if (term != 0){
                return term;
            }

            // CIRCULATE TYPE
            isSideGold = !isSideGold;
            State newRoot = new State(gamestateClone.board, isSideType(isSideGold));
            searchTreeClone.root = newRoot;

            // Visualize
            GUI(gamestateClone.board);
        }
    }*/

    // AI plays n games versus current version (I recognized that this is meaningless with this algorithm)
    /*public static void selfplayIterate(int[][] map, String type, boolean isSideGold, int depth, int numberOfIteration){
        int[] wins = new int[2]; // wins[0] -> number of gold wins, wins[1] -> number of silver wins
        for(int i = 0; i < numberOfIteration; i++){
            int result = selfplayAlphaBetaTest(map, type, isSideGold, depth);
            wins[result-1]++;
            System.out.println("RESULT: " + result);
            System.out.println(Arrays.toString(map));
        }
        System.out.println("In this iterative test (" + numberOfIteration + "):\nGold won " + wins[0] + " times\nSilver won " + wins[1] + " times");
    }*/

    public static void howManyPossibleMoves(Game gameState){
        int sum = 0;
        gameState.exploreChildren();
        ArrayList<State> nextGen = gameState.prepareChildrenStatesforTree();
        sum += nextGen.toArray().length;
        for(State nextGenS : nextGen){
            if(nextGenS.stateType.equals("GOLD2") || nextGenS.stateType.equals("SILVER2")){
                nextGenS.exploreChildren();
                sum += nextGenS.prepareChildrenStatesforTree().toArray().length;
            }
        }

        System.out.println("This is the number of all possible moves that can be done in current state: " + sum);
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

    public static void selfplayABTwoDepths(int[][] map, boolean isSideGold, int depth, int depth2){
        Game gameState = new Game(map, isSideType(isSideGold)); // First AI is Gold if isSideGold TRUE
        SearchTree searchTree = new SearchTree(gameState, depth);
        Game gameState2 = new Game(map, isSideType(!isSideGold));
        SearchTree searchTree2 = new SearchTree(gameState2, depth2);
        while(true) {
            if(isSideGold) {    // If TRUE, first AI is Gold and goes first. If FALSE, second AI should go first.
                System.out.println("First AI with depth " + depth + " plays:");
                ArrayList<Integer[]> chosenMoves = searchTree.doAlphaBetaAndReturnBestMove(depth);
                gameState.play(chosenMoves);
                State newRoot = new State(gameState.board, isSideType(isSideGold));
                searchTree.root = newRoot;
                GUI(gameState.board);
            }

            // It's showtime for the second AI
            System.out.println("Second AI with depth " + depth2 + " plays:");
            ArrayList<Integer[]> chosenMoves2 = searchTree2.doAlphaBetaAndReturnBestMove(depth2);
            gameState2.play(chosenMoves2);
            State newRoot2 = new State(gameState2.board, isSideType(!isSideGold));
            searchTree2.root = newRoot2;
            GUI(gameState2.board);

            if(!isSideGold) {    // If TRUE, first AI is Silver and should go second.
                System.out.println("First AI with depth " + depth + " plays:");
                ArrayList<Integer[]> chosenMoves = searchTree.doAlphaBetaAndReturnBestMove(depth);
                gameState.play(chosenMoves);
                State newRoot = new State(gameState.board, isSideType(isSideGold));
                searchTree.root = newRoot;
                GUI(gameState.board);
            }
        }
    }

    /*public static void selfplayABTwoEvals(int[][] map, boolean isSideGold, int depth){
        Game gameState = new Game(map, isSideType(isSideGold)); // First AI is Gold if isSideGold TRUE
        SearchTree searchTree = new SearchTree(gameState, depth);
        searchTree.

        Game gameState2 = new Game(map, isSideType(!isSideGold));
        SearchTree searchTree2 = new SearchTree(gameState2, depth2);
        while(true) {
            if(isSideGold) {    // If TRUE, first AI is Gold and goes first. If FALSE, second AI should go first.
                System.out.println("First AI with depth " + depth + " plays:");
                ArrayList<Integer[]> chosenMoves = searchTree.doAlphaBetaAndReturnBestMove(depth);
                gameState.play(chosenMoves);
                State newRoot = new State(gameState.board, isSideType(isSideGold));
                searchTree.root = newRoot;
                GUI(gameState.board);
            }

            // It's showtime for the second AI
            System.out.println("Second AI with depth " + depth2 + " plays:");
            ArrayList<Integer[]> chosenMoves2 = searchTree2.doAlphaBetaAndReturnBestMove(depth2);
            gameState2.play(chosenMoves2);
            State newRoot2 = new State(gameState2.board, isSideType(!isSideGold));
            searchTree2.root = newRoot2;
            GUI(gameState2.board);

            if(!isSideGold) {    // If TRUE, first AI is Silver and should go second.
                System.out.println("First AI with depth " + depth + " plays:");
                ArrayList<Integer[]> chosenMoves = searchTree.doAlphaBetaAndReturnBestMove(depth);
                gameState.play(chosenMoves);
                State newRoot = new State(gameState.board, isSideType(isSideGold));
                searchTree.root = newRoot;
                GUI(gameState.board);
            }
        }
    }*/


    // TO DO: CLONE ENTIRE RESPECTIVE ROW & COLUMN to decrease the number of calls made to MAP//
    // TO DO : PLEASE GET RID OF 4 LOOPS (DISGUSTING CODE)//
    // TESTING ONLY
    public static void GenerateMovesforTile(int[] coor, int[][] map) {
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
