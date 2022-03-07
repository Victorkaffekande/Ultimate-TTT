package dk.easv.bll.bot;

import dk.easv.bll.field.Field;
import dk.easv.bll.field.IField;
import dk.easv.bll.game.GameState;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class aJoshua implements IBot {
    private final String BOT_NAME = "Joshua";
    private final int[][] position = {
            {0,0},{0,1},{0,2},
            {1,0},{1,1,},{1,2},
            {2,0},{2,1},{2,2}};


    @Override
    public IMove doMove(IGameState state) {
        //minimax

        miniMax(state, state.getField(), 1,true);

        return state.getField().getAvailableMoves().get(3);
    }

    @Override
    public String getBotName() {
        return BOT_NAME;
    }

    public IMove miniMax(IGameState state,IField position, int depth, boolean maximizingPlayer) {
        if (depth == 0){
            return new Move(1,2);// fix. Skal være statisk evel
        }

        if (maximizingPlayer){
            List<IMove> availMoves = position.getAvailableMoves();
            double maxEval = Double.NEGATIVE_INFINITY;
            int count = 0;
            IGameState tmpState=null;
            for (IMove m : availMoves){
                count++;
                GameSimulator simulator = createSimulator(state);
                if(simulator.updateGame(m)){
                   // System.out.println(simulator.getCurrentState().getMoveNumber());
                    tmpState = simulator.getCurrentState();
                    System.out.println(tmpState);
                }
/***
 * fuck
 */

            }
        }




/*
        if (maximizingPlayer){

            String[][] macroBoard = position.getMacroboard();

            int i = 0;
            for(String[] s : macroBoard)
            {
                i++;
            }
            System.out.println(i);
            for (String[] pos : position.getBoard()){
                new Move(Integer.parseInt(pos[0]),Integer.parseInt(pos[1]));

            }


            //list af all 3X3 boards der må spilles
            List<String[][]> availFields = new ArrayList<>();
            for(int x = 0; x<3 ;x++){
                for(int y = 0; y<3; y++){
                    if (state.getField().isInActiveMicroboard(x,y)){
                        availFields.add(state.getField().getBoard());
                    }
                }
            }

            for (String[][] board : availFields){
                String[][] boardClone = Arrays.stream(board).map(String[]::clone).toArray(String[][]::new);

            }
        }
 */
        return new Move(1,2);
    }



    private double evaluateGame(Move move, IField currentField, List<IMove> availMoves) {
        double evaluation = 0;
        Field mainBoard;
        double[] evaluationMultiplier = {1.4, 1, 1.4, 1, 1.75, 1, 1.4, 1, 1.4};


        for (int i = 0; i < 9; i++) {
            evaluation += realEvaluateSquare(position[i]) * 1.5 * evaluationMultiplier[i];        }

        return 1.1;
    }

    private double realEvaluateSquare(int[] pos) {
        return 1.2;
    }


    public int checkWinCondition(Field field) {
        int a = -1;
        if (checkRowsForWin(field) || checkColumnsForWin(field) || checkDiagonalsForWin(field)) {
            return a;
        }
        a = 1;
        if (checkRowsForWin(field) || checkColumnsForWin(field) || checkDiagonalsForWin(field)) {
            return a;
        }
        return 0;
    }

    /**
     * checks if a row / col has the same value
     */
    private boolean checkRowCol(String c1, String c2, String c3) {
        return ((!Objects.equals(c1, IField.EMPTY_FIELD)) && (Objects.equals(c1, c2)) && (Objects.equals(c2, c3)));
    }

    /**
     * Checks the rows for a possible win.
     *
     * @return true if there is three matching symbols in a row
     */
    private boolean checkRowsForWin(IField field) {
        for (int i = 0; i < 3; i++) {
            String[][] gameBoard = field.getBoard();
            if (checkRowCol(gameBoard[i][0], gameBoard[i][1], gameBoard[i][2])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks the columns for a possible win.
     *
     * @return true if there is three matching symbols in a column
     */
    private boolean checkColumnsForWin(IField field) {
        for (int i = 0; i < 3; i++) {
            String[][] gameBoard = field.getBoard();
            if (checkRowCol(gameBoard[0][i], gameBoard[1][i], gameBoard[2][i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks the diagonals for a possible win.
     *
     * @return true if there is three matching symbols in a diagonal
     */
    private boolean checkDiagonalsForWin(IField field) {
        String[][] gameBoard = field.getBoard();
        return ((checkRowCol(gameBoard[0][0], gameBoard[1][1], gameBoard[2][2])) || (checkRowCol(gameBoard[0][2], gameBoard[1][1], gameBoard[2][0])));
    }
    public enum GameOverState {
        Active,
        Win,
        Tie
    }
    private GameSimulator createSimulator(IGameState state) {
        GameSimulator simulator = new GameSimulator(new GameState());
        simulator.setGameOver(GameOverState.Active);
        simulator.setCurrentPlayer(state.getMoveNumber() % 2);
        simulator.getCurrentState().setRoundNumber(state.getRoundNumber());
        simulator.getCurrentState().setMoveNumber(state.getMoveNumber());
        simulator.getCurrentState().getField().setBoard(state.getField().getBoard());
        simulator.getCurrentState().getField().setMacroboard(state.getField().getMacroboard());
        return simulator;
    }

    class GameSimulator {
        private final IGameState currentState;
        private int currentPlayer = 0; //player0 == 0 && player1 == 1
        private volatile GameOverState gameOver = GameOverState.Active;

        public void setGameOver(GameOverState state) {
            gameOver = state;
        }

        public GameOverState getGameOver() {
            return gameOver;
        }

        public void setCurrentPlayer(int player) {
            currentPlayer = player;
        }

        public IGameState getCurrentState() {
            return currentState;
        }

        public GameSimulator(IGameState currentState) {
            this.currentState = currentState;
        }

        public Boolean updateGame(IMove move) {
            if (!verifyMoveLegality(move))
                return false;

            updateBoard(move);
            currentPlayer = (currentPlayer + 1) % 2;

            return true;
        }

        private Boolean verifyMoveLegality(IMove move) {
            IField field = currentState.getField();
            boolean isValid = field.isInActiveMicroboard(move.getX(), move.getY());

            if (isValid && (move.getX() < 0 || 9 <= move.getX())) isValid = false;
            if (isValid && (move.getY() < 0 || 9 <= move.getY())) isValid = false;

            if (isValid && !field.getBoard()[move.getX()][move.getY()].equals(IField.EMPTY_FIELD))
                isValid = false;

            return isValid;
        }

        private void updateBoard(IMove move) {
            String[][] board = currentState.getField().getBoard();
            board[move.getX()][move.getY()] = currentPlayer + "";
            currentState.setMoveNumber(currentState.getMoveNumber() + 1);
            if (currentState.getMoveNumber() % 2 == 0) {
                currentState.setRoundNumber(currentState.getRoundNumber() + 1);
            }
            checkAndUpdateIfWin(move);
            updateMacroboard(move);

        }

        private void checkAndUpdateIfWin(IMove move) {
            String[][] macroBoard = currentState.getField().getMacroboard();
            int macroX = move.getX() / 3;
            int macroY = move.getY() / 3;

            if (macroBoard[macroX][macroY].equals(IField.EMPTY_FIELD) ||
                    macroBoard[macroX][macroY].equals(IField.AVAILABLE_FIELD)) {

                String[][] board = getCurrentState().getField().getBoard();

                if (isWin(board, move, "" + currentPlayer))
                    macroBoard[macroX][macroY] = currentPlayer + "";
                else if (isTie(board, move))
                    macroBoard[macroX][macroY] = "TIE";

                //Check macro win
                if (isWin(macroBoard, new Move(macroX, macroY), "" + currentPlayer))
                    gameOver = GameOverState.Win;
                else if (isTie(macroBoard, new Move(macroX, macroY)))
                    gameOver = GameOverState.Tie;
            }

        }

        private boolean isTie(String[][] board, IMove move) {
            int localX = move.getX() % 3;
            int localY = move.getY() % 3;
            int startX = move.getX() - (localX);
            int startY = move.getY() - (localY);

            for (int i = startX; i < startX + 3; i++) {
                for (int k = startY; k < startY + 3; k++) {
                    if (board[i][k].equals(IField.AVAILABLE_FIELD) ||
                            board[i][k].equals(IField.EMPTY_FIELD))
                        return false;
                }
            }
            return true;
        }


        public boolean isWin(String[][] board, IMove move, String currentPlayer) {
            int localX = move.getX() % 3;
            int localY = move.getY() % 3;
            int startX = move.getX() - (localX);
            int startY = move.getY() - (localY);

            //check col
            for (int i = startY; i < startY + 3; i++) {
                if (!board[move.getX()][i].equals(currentPlayer))
                    break;
                if (i == startY + 3 - 1) return true;
            }

            //check row
            for (int i = startX; i < startX + 3; i++) {
                if (!board[i][move.getY()].equals(currentPlayer))
                    break;
                if (i == startX + 3 - 1) return true;
            }

            //check diagonal
            if (localX == localY) {
                //we're on a diagonal
                int y = startY;
                for (int i = startX; i < startX + 3; i++) {
                    if (!board[i][y++].equals(currentPlayer))
                        break;
                    if (i == startX + 3 - 1) return true;
                }
            }

            //check anti diagonal
            if (localX + localY == 3 - 1) {
                int less = 0;
                for (int i = startX; i < startX + 3; i++) {
                    if (!board[i][(startY + 2) - less++].equals(currentPlayer))
                        break;
                    if (i == startX + 3 - 1) return true;
                }
            }
            return false;
        }

        private void updateMacroboard(IMove move) {
            String[][] macroBoard = currentState.getField().getMacroboard();
            for (int i = 0; i < macroBoard.length; i++)
                for (int k = 0; k < macroBoard[i].length; k++) {
                    if (macroBoard[i][k].equals(IField.AVAILABLE_FIELD))
                        macroBoard[i][k] = IField.EMPTY_FIELD;
                }

            int xTrans = move.getX() % 3;
            int yTrans = move.getY() % 3;

            if (macroBoard[xTrans][yTrans].equals(IField.EMPTY_FIELD))
                macroBoard[xTrans][yTrans] = IField.AVAILABLE_FIELD;
            else {
                // Field is already won, set all fields not won to avail.
                for (int i = 0; i < macroBoard.length; i++)
                    for (int k = 0; k < macroBoard[i].length; k++) {
                        if (macroBoard[i][k].equals(IField.EMPTY_FIELD))
                            macroBoard[i][k] = IField.AVAILABLE_FIELD;
                    }
            }
        }
    }
}
