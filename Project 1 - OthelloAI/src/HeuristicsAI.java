import java.util.ArrayList;

public class HeuristicsAI implements IOthelloAI {

    // In Othello corners are key to winning, therefore corners
    // are prioritized highly in our evaluation function
    private double cornerPriority = 0.8;
    private double tokensPriority = 0.2;

    // Describes the maximum depth that will allow the recursive calls
    // to continue down, this also depends on the amount of legal moves
    // on each recursive call
    private int maxDepth = 75;

    public HeuristicsAI() { }

    public Position decideMove(GameState s) {
        long startTime = System.currentTimeMillis();
        ArrayList<Position> moves = s.legalMoves();

        Position chosenPosition;

        if (!moves.isEmpty()) {
            chosenPosition = getBestMove(s);
        } else {
            chosenPosition = new Position(-1, -1);
        }

        System.out.println("Took: " + (System.currentTimeMillis() - startTime));

        return chosenPosition;
    }


    /**
     * Returns a number between -1 and 1, depending on which player
     * is most likely to win at the given game state.
     * Positive numbers favours player one, negative numbers favour
     * player two.
     */
    private double calculateHeuristics(GameState s) {
        return (cornerPriority * calculateCorners(s)) + (tokensPriority * calculateTokens(s));
    }


    /**
     * Returns a number between -1 and 1, depending on which player
     * possesses the corners
     */
    private double calculateCorners(GameState s) {
        int[][] board = s.getBoard();

        double value = 0;

        int topLeft = board[0][0];
        value += topLeft == 0 ? 0 : topLeft == 1 ? 0.25 : -0.25;

        int topRight = board[0][board[1].length-1];
        value += topRight == 0 ? 0 : topRight == 1 ? 0.25 : -0.25;

        int bottomLeft = board[board[0].length-1][0];
        value += bottomLeft == 0 ? 0 : bottomLeft == 1 ? 0.25 : -0.25;

        int bottomRight = board[board[0].length-1][board[1].length-1];
        value += bottomRight == 0 ? 0 : bottomRight == 1 ? 0.25 : -0.25;

        return value;
    }

    /**
     * Returns a number between -1 and 1, depending on which player
     * has the most tokens
     */
    private double calculateTokens(GameState s) {
        int[] tokens = s.countTokens();
        return (tokens[0] - tokens[1]) / (tokens[0] + tokens[1]);
    }

    /**
     * Method that'll initialize the alphabeta function that'll determine the
     * best possible move based on the current game state, and then returns
     * the best move.
     */
    private Position getBestMove(GameState s) {
        ArrayList<Position> moves = s.legalMoves();
        Position chosenPosition = moves.get(0);

        // Initilize the current highest and lowest beta
        double highestAlpha = Integer.MIN_VALUE;
        double lowestBeta = Integer.MAX_VALUE;

        // If player == MAX
        if (s.getPlayerInTurn() == 1) {
            for (Position move : moves) {
                // Creating a new GameState to not interfere with the
                // "real" current GameState
                GameState childState = new GameState(s.getBoard(), s.getPlayerInTurn());
                childState.insertToken(move);

                // Searches recursively for the best possible outcome from current move.
                double currentAlpha = alphabetaHeuristics(childState, highestAlpha, lowestBeta, 0);

                // If currentAlpha is larger that highestAlpha, then this position has
                // a better outcome.
                if(currentAlpha > highestAlpha) {
                    // ...hence we update our currently chosen position and the
                    // highest alpha
                    chosenPosition = move;
                    highestAlpha = currentAlpha;
                }
            }

            return chosenPosition;
        } else {
            // ... else if player == MIN
            for (Position move : moves) {
                // Creating a new GameState to not interfere with the
                // "real" current GameState
                GameState childState = new GameState(s.getBoard(), s.getPlayerInTurn());
                childState.insertToken(move);

                // Searches recursively for the best possible outcome from current move.
                double currentBeta = alphabetaHeuristics(childState, highestAlpha, lowestBeta, 0);

                // If currentBeta is lower that lowestBeta, then this position has
                // a better outcome.
                if(currentBeta < lowestBeta) {
                    // ...hence we update our currently chosen position and the
                    // lowest beta
                    chosenPosition = move;
                    lowestBeta = currentBeta;
                }
            }

            return chosenPosition;
        }
    }

    /**
     * Algorithm that'll recursively traverse down the game tree in order to
     * determine either the lowest possible beta or the highest possible alpha
     * based on the player.
     */
    private double alphabetaHeuristics(GameState s, double alpha, double beta, int depth) {
        ArrayList<Position> moves = s.legalMoves();

        // If the game reaches finished state, it should simply return whether
        // we've won or not
        if (s.isFinished()) {
            return calculateTokens(s);
        }

        // Give turn to other player if current player have no legal moves
        if (moves.size() == 0) {
            s.changePlayer();
            return alphabetaHeuristics(s, alpha, beta, depth + 1);
        }

        // If we've reached our cut-off depth, return heuristics value
        if (depth >= maxDepth/moves.size()) {
            return calculateHeuristics(s);
        }

        // Player MAX
        if (s.getPlayerInTurn() == 1) {
            // Go through all available moves in the current game state
            for (Position move : moves) {
                GameState clonedState = new GameState(s.getBoard(), s.getPlayerInTurn());
                clonedState.insertToken(move);

                // Searches recursively for the highest possible alpha from current move.
                alpha = Math.max(alpha, alphabetaHeuristics(clonedState, alpha, beta, depth + 1));

                // beta cut-off
                if (beta <= alpha) {
                    break;
                }
            }

            return alpha;
        } else {
            // Player MIN
            // Go through all available moves in the current game state
            for (Position move : moves) {
                GameState clonedState = new GameState(s.getBoard(), s.getPlayerInTurn());
                clonedState.insertToken(move);

                // Searches recursively for the lowest possible beta from current move.
                beta = Math.min(beta, alphabetaHeuristics(clonedState, alpha, beta, depth + 1));

                // alpha cut-off
                if (beta <= alpha) {
                    break;
                }
            }

            return beta;
        }
    }
}
