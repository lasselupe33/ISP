import net.sf.javabdd.*;

public class QueensLogic implements IQueensLogic {

    private int size;		// Size of quadratic game board (i.e. size = #rows = #columns)
    private int[][] board;	// Content of the board. Possible values: 0 (empty), 1 (queen), -1 (no queen allowed)

    private BDDFactory fact;
    private BDD True;
    private BDD False;
    private BDD rootBDD;


    @Override
    public void initializeBoard(int size) {
        this.size = size;
        board = new int[size][size];

        InitializeBDD();
        updateInvalidPositions();
    }

    @Override
    public int[][] getBoard() {
        return board;
    }

    @Override
    public void insertQueen(int column, int row) {

        // Check whether position is empty
        if (board[column][row] == -1 || board[column][row] == 1) {

            // If not, just return
            return;
        }

        // Place a Queen on given position
        board[column][row] = 1;

        // Updates the rootBDD by the new restriction of the new placed
        // queen
        rootBDD = rootBDD.restrict(getVariable(column, row));

        // Updates the board
        updateInvalidPositions();
    }

    /**
     * Method that initialize the process of building the BDD
     */
    private void InitializeBDD() {

        // Initialize the fields
        fact = JFactory.init(2000000,200000);
        True = fact.one();
        False = fact.zero();
        rootBDD = True;

        // The amount of variables are the amount of
        // position on the board
        int nVars = size*size;

        // Initialize the variable number in the BDD factory
        fact.setVarNum(nVars);

        // Build the BDD by creating the rules for all
        // variables/positions
        createRules();
    }

    /**
     * Method that creates the rules in the BDD in the correct order
     */
    private void createRules() {
        // Create rule for the individual position, affects columns,
        // rows and diagonal, since that is how Queens behave
        for (int column = 0; column < board.length; column++) {
            for (int row = 0; row < board[column].length; row++) {
                createRuleForQueensPosition(column, row);
            }
        }

        // Create rule that ensure that there can be a queen on
        // every row
        createQueenOnEveryColumnRule();
    }

    /**
     * Method that for every given position on given column and row,
     * create the rules connected to that position
     */
    private void createRuleForQueensPosition(int column, int row) {
        BDD n = True;
        BDD subBDD = False;


        // All column cells on rows position should be added
        // left --> right
        for (int c = 0; c < size; c++) {
            if(column != c) {
                // It should be 'and', since if one queen is placed, the
                // whole row should return true (true that it is invalid)
                n = n.and(getNotVariable(c, row));
            }
        }

        // All row cells on columns position should be added
        // top --> bottom
        for (int r = 0; r < size; r++) {
            if(row != r) {
                n = n.and(getNotVariable(column, r));
            }
        }


        int c = column; int r = row;

        // Diagonal top left --> bottom right
        while (r < size && c < size) {

            if(c != column && r != row) {
                n = n.and(getNotVariable(c, r));
            }

            c++; r++;
        }

        int c2 = column; int r2 = row;

        // Diagonal bottom left --> top right
        while (r2 >= 0 && c2 < size) {

            if(c2 != column && r2 != row) {
                n = n.and(getNotVariable(c2, r2));
            }

            c2++; r2--;
        }

        // Since subBDD initially is false, then the negated position
        // at given column and row decide if it is true
        subBDD = subBDD.or(getNotVariable(column, row));

        // If given position is true, then all positions in the given rules
        // is true
        subBDD = subBDD.or(n);

        // Link it together with all other rules
        rootBDD = rootBDD.and(subBDD);
    }

    /**
     * Method that creates the rule that ensures a queen
     * must be able to be placed at every row
     */
    private void createQueenOnEveryColumnRule() {

        // There must be a queen on every row, this rule checks
        // that for given position, if there can't be a queen on
        // every row, the position is invalid
        for (int row = 0; row < size; row++) {
            BDD subBDD = False;

            for (int column = 0; column < size; column++) {
                subBDD = subBDD.or(getVariable(column, row));
            }

            //sub_bdd must be true
            rootBDD = rootBDD.and(subBDD);
        }
    }

    /**
     *  Get variable in BDD factory on given position
     */
    private BDD getVariable(int column, int row) {
        return fact.ithVar(row * size + column);
    }

    /**
     *  Get negation of variable in BDD factory on given position
     */
    private BDD getNotVariable(int column, int row) {
        return fact.nithVar(row * size + column);
    }

    /**
     * Helper method that checks whether the given position is
     * invalid
     */
    private boolean isPositionInvalid(int column, int row) {
        // Creates a new BDD restricted to given position
        BDD positionBDD = rootBDD.restrict(getVariable(column, row));

        // If position is invalid, return true
        return positionBDD.isZero();
    }

    /**
     * Method that updates invalid positions every time a queen
     * is placed
     */
    private void updateInvalidPositions() {

        // Counter to help determine the remaining valid positions
        int invalidCellsCounter = 0;

        for (int column = 0; column < board.length; column++) {
            for (int row = 0; row < board[column].length; row++) {
                if (isPositionInvalid(column, row)) {

                    // If position is invalid, mark it in the board
                    board[column][row] = -1;

                    invalidCellsCounter++;
                }
            }
        }

        // Checking the remaining valid positions
        checkRemainingValidPositions(invalidCellsCounter);
    }

    /**
     * Helper method that checks whether the remaining valid positions
     * are equal to size
     */
    private void checkRemainingValidPositions(int invalidCells) {

        int validCells = size*size - invalidCells;

        if(validCells == size) {

            // If true then on the remaining valid positions a Queen
            // should be placed
            for (int column = 0; column < board.length; column++) {
                for (int row = 0; row < board[column].length; row++) {
                    if (board[column][row] == 0) {
                        board[column][row] = 1;
                    }
                }
            }
        }
    }
}