import net.sf.javabdd.*;

public class QueensLogic implements IQueensLogic {

    private int size;		// Size of quadratic game board (i.e. size = #rows = #columns)
    private int[][] board;	// Content of the board. Possible values: 0 (empty), 1 (queen), -1 (no queen allowed)

    private BDDFactory fact;
    private BDD True;
    private BDD False;
    private BDD bdd;


    @Override
    public void initializeBoard(int size) {
        this.size = size;
        board = new int[size][size];

        // Initializing all positions on the board

        InitializeBDD();
        updateInvalidPositions();
    }

    @Override
    public int[][] getBoard() {
        return board;
    }

    @Override
    public void insertQueen(int column, int row) {

        if (board[column][row] == -1 || board[column][row] == 1) {
            return;
        }
        board[column][row] = 1;

        bdd = bdd.restrict(getVariable(column, row));

        updateInvalidPositions();
    }

    private void InitializeBDD() {
        int nVars = size*size;

        fact = JFactory.init(2000000,200000);
        True = fact.one();
        False = fact.zero();
        bdd = True;

        fact.setVarNum(nVars);

        for (int column = 0; column < board.length; column++) {
            for (int row = 0; row < board[column].length; row++) {
                createRuleForPosition(column, row);
            }
        }

        for (int row = 0; row < size; row++) {
            BDD sub_bdd = False;

            for (int column = 0; column < size; column++) {
                sub_bdd = sub_bdd.or(getVariable(column, row));
            }

            //sub_bdd must be true
            bdd = bdd.and(sub_bdd);
        }
    }

    private void createRuleForPosition(int column, int row) {
        BDD n = True;
        BDD subBDD = False;

        // All column cells on rows position should be added
        for (int c = 0; c < size; c++) {
            if(column != c) {
                // It should be 'and', since if one queen is placed, the
                // whole row should return true (true that it is invalid)
                n = n.and(getNotVariable(c, row));
            }
        }

        // All row cells on columns position should be added
        for (int r = 0; r < size; r++) {
            if(row != r) {
                n = n.and(getNotVariable(column, r));
            }
        }


        int c = column;
        int r = row;

        // Diagonal top left --> bottom right
        while (r < size && c < size) {

            if(c != column && r != row) {
                n = n.and(getNotVariable(c, r));
            }

            c++;
            r++;
        }

        int c2 = column;
        int r2 = row;

        // Diagonal bottom left --> top right
        while (r2 >= 0 && c2 < size) {

            if(c2 != column && r2 != row) {
                n = n.and(getNotVariable(c2, r2));
            }

            c2++;
            r2--;
        }

        subBDD = subBDD.or(getNotVariable(column, row));

        subBDD = subBDD.or(n);

        bdd = bdd.and(subBDD);
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

    private boolean isPositionInvalid(int column, int row) {
        BDD position = bdd.restrict(getVariable(column, row));

        return position.isZero();
    }

    private void updateInvalidPositions() {

        for (int column = 0; column < board.length; column++) {
            for (int row = 0; row < board[column].length; row++) {
                if (isPositionInvalid(column, row)) {

                    board[column][row] = -1;
                }
            }
        }
    }
}

