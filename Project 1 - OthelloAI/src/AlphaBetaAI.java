import java.util.ArrayList;

class AlphaBetaAI implements IOthelloAI {

    public AlphaBetaAI() { }

    public Position decideMove(GameState s) {
        ArrayList<Position> moves = s.legalMoves();

        if (!moves.isEmpty()) {
            return getBestMove(s);
        } else {
            return new Position(-1, -1);
        }
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
        int highestAlpha = Integer.MIN_VALUE;
        int lowestBeta = Integer.MAX_VALUE;

        // If player == MAX
        if (s.getPlayerInTurn() == 1) {
            for (Position move : moves) {
                // Creating a new GameState to not interfere with the
                // "real" current GameState
                GameState childState = new GameState(s.getBoard(), s.getPlayerInTurn());
                childState.insertToken(move);

                // Searches recursively for the best possible outcome from current move.
                int currentAlpha = alphabeta(childState, highestAlpha, lowestBeta);

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
                int currentBeta = alphabeta(childState, highestAlpha, lowestBeta);

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
    private int alphabeta(GameState s, int alpha, int beta) {
        // If in terminal state, return value.
        if (s.isFinished()) {
            int[] tokens = s.countTokens();

            // player 1 token subtracted with player 2 token.
            // If outcome is positive, player 1 have won.
            // If negative, player 2 have won.
            return tokens[0] - tokens[1];
        }

        ArrayList<Position> moves = s.legalMoves();

        // Player MAX
        if (s.getPlayerInTurn() == 1) {
            // Go through all available moves in the current game state
            for (Position move : moves) {
                s.insertToken(move);

                // Searches recursively for the highest possible alpha from current move.
                alpha = Math.max(alpha, alphabeta(s, alpha, beta));

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
                s.insertToken(move);

                // Searches recursively for the lowest possible beta from current move.
                beta = Math.min(beta, alphabeta(s, alpha, beta));

                // alpha cut-off
                if (beta <= alpha) {
                    break;
                }
            }

            return beta;
        }
    }
}
