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

        /*for (int c = 0; c < board.length; c++) {
            for (int r = 0; r < board[c].length; r++) {
                System.out.println(c + ", " + r + ": " + board[c][r]);
            }
        }*/

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
    }

    private void createRuleForPosition(int column, int row) {
        BDD n = True;
        BDD subBDD = False;

        for (int c = 0; c < row; c++) {
            if(column != c) {
                n = n.and(getNotVariable(c, row));
            }
        }

        for (int r = 0; r < column; r++) {
            if(row != r) {
                n = n.and(getNotVariable(column, r));
            }
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