package com.congklakuas2;

public class CongklakGame {
    private final CongklakBoard board;
    private int currentPlayer; // 0 = Player 1, 1 = Player 2

    public CongklakGame() {
        board = new CongklakBoard();
        currentPlayer = CongklakBoard.P1;
    }

    public CongklakBoard getBoard() {
        return board;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isPitPlayable(int pitIndex) {
        return board.isOwnPit(currentPlayer, pitIndex) && board.getSeedAt(pitIndex) > 0;
    }

    public MoveResult makeMove(int pitIndex) {
        return board.makeMove(currentPlayer, pitIndex);
    }

    public void switchTurn() {
        currentPlayer = (currentPlayer == CongklakBoard.P1) ? CongklakBoard.P2 : CongklakBoard.P1;
    }

    public boolean isGameOver() {
        return board.isGameOver();
    }

    public void collectRemainingSeeds() {
        board.collectRemainingSeeds();
    }
}
