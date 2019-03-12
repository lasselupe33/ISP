import java.util.ArrayList;

class MiniMaxAI implements IOthelloAI {

    public MiniMaxAI() { }


    public Position decideMove(GameState s) {
        ArrayList<Position> moves = s.legalMoves();

        if (!moves.isEmpty()) {
            return initMiniMax(s);
        }
        else
            return new Position(-1, -1);

    }


    /**
     *  The root minmax that starts at the current GameState.
     *  Searches through each of the legal moves, and chooses the best
     *  position based on terminal outcome.
     * */

    private Position initMiniMax(GameState s) {
        ArrayList<Position> moves = s.legalMoves();
        Position chosenPosition = moves.get(0);


        // If player 1
        if (s.getPlayerInTurn() == 1) {
            int highestValue = Integer.MIN_VALUE;

            for (Position move : moves) {

                GameState childState = new GameState(s.getBoard(), s.getPlayerInTurn());
                childState.insertToken(move);

                // Searches recursively for the best possible outcome from current move.
                int currentValue = minimax(childState, 0);

                // If currentValue is larger that highestValue, then this position has
                // a better outcome.
                if(currentValue > highestValue) {
                    chosenPosition = move;
                    highestValue = currentValue;
                }
            }
            return chosenPosition;
        }

        // If player 2
        else {
            int lowestValue = Integer.MAX_VALUE;

            for (Position move : moves) {

                // Creating a new GameState to not interfere with the
                // "real" current GameState
                GameState childState = new GameState(s.getBoard(), s.getPlayerInTurn());

                childState.insertToken(move);

                // Searches recursively for the best possible outcome from current move
                int currentValue = minimax(childState, 0);

                // If currentValue is lower that lowestValue, then this position has
                // a better outcome.
                if(currentValue < lowestValue) {
                    chosenPosition = move;
                    lowestValue = currentValue;
                }
            }

            return chosenPosition;
        }

    }


    /**
     *  A recursive helper that finds the best possible outcome of
     *  each state.
     * */

    private int minimax(GameState s, int depth) {
        // If in terminal state, return value.
        if(s.isFinished()) {
            int[] tokens = s.countTokens();

            // player 1 token subtracted with player 2 token.
            // If outcome is positive, player 1 have won.
            // If negative, player 2 have won.
            return tokens[0] - tokens[1];
        }

        ArrayList<Position> moves = s.legalMoves();

        if (moves.size() == 0) {
            s.changePlayer();

            return minimax(s, depth + 1);
        }

        // If player 1
        if (s.getPlayerInTurn() == 1) {
            int value = Integer.MIN_VALUE;

            for (Position move : moves) {
                GameState clonedState = new GameState(s.getBoard(), s.getPlayerInTurn());
                clonedState.insertToken(move);

                value = Math.max(value, minimax(clonedState, depth + 1));
            }

            return value;
        }

        // If player 2
        else {
            int value = Integer.MAX_VALUE;
            for (Position move : moves) {
                GameState clonedState = new GameState(s.getBoard(), s.getPlayerInTurn());
                clonedState.insertToken(move);

                value = Math.min(value, minimax(clonedState, depth + 1));
            }

            return value;
        }

    }

}