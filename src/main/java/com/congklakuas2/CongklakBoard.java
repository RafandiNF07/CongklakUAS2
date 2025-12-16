package com.congklakuas2;

import java.util.ArrayList;

public class CongklakBoard {
    public static final int P1 = 0;
    public static final int P2 = 1;

    private int[] board = new int[16];

    public CongklakBoard() {
        resetBoard();
    }

    public void resetBoard() {
        for (int i = 0; i < 16; i++) board[i] = 0;
        for (int i = 0; i <= 6; i++) board[i] = 7;
        for (int i = 8; i <= 14; i++) board[i] = 7;
        board[7] = 0;
        board[15] = 0;
    }

    public int[] getBoard() { return board; }
    public int getSeedAt(int index) { return board[index]; }

    public boolean isOwnPit(int player, int pos) {
        if (player == P1) return pos >= 0 && pos <= 6;
        return pos >= 8 && pos <= 14;
    }

    public int ownStoreIdx(int player) { return (player == P1) ? 7 : 15; }
    public int oppStoreIdx(int player) { return (player == P1) ? 15 : 7; }

    public int oppositePos(int pos) {
        if ((pos >= 0 && pos <= 6) || (pos >= 8 && pos <= 14)) return 14 - pos;
        return -1;
    }

    public MoveResult makeMove(int currentPlayer, int pitIndex) {
        MoveResult result = new MoveResult();
        result.startPit = pitIndex;
        result.path = new ArrayList<>();

        if (!isOwnPit(currentPlayer, pitIndex) || board[pitIndex] == 0) {
            result.valid = false;
            return result;
        }
        result.valid = true;

        int seeds = board[pitIndex];
        result.startSeeds = seeds;
        board[pitIndex] = 0;

        int pos = pitIndex;

        boolean passedOpponent = false;

        while (true) {

            while (seeds > 0) {
                pos = (pos + 1) % 16;

                if (pos == oppStoreIdx(currentPlayer)) {
                    continue;
                }

                if (!isOwnPit(currentPlayer, pos) && pos != ownStoreIdx(currentPlayer)) {
                    passedOpponent = true;
                }

                board[pos]++;
                result.path.add(pos);
                seeds--;
            }

            result.stopAtIndex = pos;


            if (pos == ownStoreIdx(currentPlayer)) {
                result.freeTurn = true;
                return result;
            }

            if (board[pos] > 1) {
                seeds = board[pos];
                board[pos] = 0;
                continue;
            }

            else if (isOwnPit(currentPlayer, pos)) {

                if (passedOpponent) {
                    int opp = oppositePos(pos);
                    if (opp != -1 && board[opp] > 0) {
                        int captured = board[opp] + board[pos];

                        board[opp] = 0;
                        board[pos] = 0;
                        board[ownStoreIdx(currentPlayer)] += captured;

                        result.hitOpponent = true;
                        result.capturedSeeds = captured;
                    }
                }
                break;
            } else {
                break;
            }
        }
        return result;
    }

    public boolean isGameOver() {
        boolean p1Empty = true;
        boolean p2Empty = true;
        for (int i = 0; i <= 6; i++) if (board[i] != 0) p1Empty = false;
        for (int i = 8; i <= 14; i++) if (board[i] != 0) p2Empty = false;
        return p1Empty || p2Empty;
    }

    public void collectRemainingSeeds() {
        for (int i = 0; i <= 6; i++) { board[7] += board[i]; board[i] = 0; }
        for (int i = 8; i <= 14; i++) { board[15] += board[i]; board[i] = 0; }
    }

    public int getWinner() {
        if (board[7] > board[15]) return 1;
        if (board[15] > board[7]) return 2;
        return 0;
    }
}