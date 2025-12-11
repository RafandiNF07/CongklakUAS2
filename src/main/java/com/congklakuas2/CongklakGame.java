package com.congklakuas2;

public class CongklakGame {

    private CongklakBoard board;
    private int currentPlayer = CongklakBoard.P1;

    public CongklakGame() {
        board = new CongklakBoard();
    }

    public CongklakBoard getBoard() {
        return board;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == 0) ? 1 : 0;
    }

    public boolean isGameOver() {
        return board.isGameOver();
    }

    public MoveResult makeMove(int pit) {
        MoveResult result = board.makeMove(currentPlayer, pit);

        if (result.valid && !result.freeTurn)
            currentPlayer = (currentPlayer == CongklakBoard.P1) ? CongklakBoard.P2 : CongklakBoard.P1;

        return result;
    }
}
