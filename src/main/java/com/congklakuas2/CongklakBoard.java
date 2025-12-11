package com.congklakuas2;

public class CongklakBoard {
    public static final int P1 = 0;
    public static final int P2 = 1;

    private int[] board = new int[16];

    public CongklakBoard() {
        for (int i = 0; i < 16; i++) board[i] = 0;
        for (int i = 0; i <= 6; i++) board[i] = 7;
        for (int i = 8; i <= 14; i++) board[i] = 7;
        board[7] = 0;
        board[15] = 0;
    }

    public int[] getBoard() {
        return board;
    }

    public int getSeedAt(int index) {
        return board[index];
    }

    public void setBoard(int[] newBoard) {
        board = newBoard;
    }

    public boolean isOwnPit(int player, int pos) {
        if (player == P1) return pos >= 0 && pos <= 6;
        return pos >= 8 && pos <= 14;
    }

    public int ownStoreIdx(int player) {
        return (player == P1) ? 7 : 15;
    }

    public int oppStoreIdx(int player) {
        return (player == P1) ? 15 : 7;
    }

    public int oppositePos(int pos) {
        if ((pos >= 0 && pos <= 6) || (pos >= 8 && pos <= 14))
            return 14 - pos;
        return -1;
    }

    public int pitsSum(int player) {
        int sum = 0;
        if (player == P1) for (int i = 0; i <= 6; i++) sum += board[i];
        else for (int i = 8; i <= 14; i++) sum += board[i];
        return sum;
    }

    public MoveResult makeMove(int currentPlayer, int pitIndex) {
        MoveResult result = new MoveResult();
        result.startPit = pitIndex;
        result.path = new java.util.ArrayList<>();

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

        result.path.add(pitIndex);

        while (true) {
            while (seeds > 0) {
                pos = (pos + 1) % 16;
                if (pos == oppStoreIdx(currentPlayer)) {
                    pos = (pos + 1) % 16;
                }

                board[pos]++;
                result.path.add(pos);

                if (isOwnPit(currentPlayer == P1 ? P2 : P1, pos)) {
                    passedOpponent = true;
                }
                seeds--;
            }
            if (pos == ownStoreIdx(currentPlayer)) {
                result.freeTurn = true;
                return result;
            }
            if (pos != ownStoreIdx(P1) && pos != ownStoreIdx(P2)) {
                if (board[pos] > 1) {
                    seeds = board[pos];
                    board[pos] = 0;

                    result.path.add(pos);
                    continue;
                    
                } else {
                    if (passedOpponent && isOwnPit(currentPlayer, pos)) {
                        int opp = oppositePos(pos);
                        if (opp >= 0 && board[opp] > 0) {
                            int captured = board[opp];
                            board[opp] = 0;
                            board[pos] = 0;
                            board[ownStoreIdx(currentPlayer)] += captured + 1;
                        }    
                    }
                }
            }
            break;
        }
        return result;
    }
    public boolean isGameOver() {
        boolean p1Empty = true;
        boolean p2Empty = true;

        for (int i = 0; i < 7; i++) {
            if (board[i] != 0) {
                p1Empty = false;
                break;
            }
        }
        for (int i = 8; i < 15; i++) {
            if (board[i] != 0) {
                p2Empty = false;
            break;
            }
        }
        return p1Empty || p2Empty;
    }

    public void collectRemainingSeeds() {
        int sum1 = 0;
        for (int i = 0; i < 7; i++) {
            sum1 += board[i];
            board[i] = 0;
        }
        board[7] += sum1;

        int sum2 = 0;
        for (int i = 8; i < 15; i++) {
            sum2 += board[i];
            board[i] = 0;
        }
        board[15] += sum2;
    }

    public int getWinner() {
        int p1 = board[7];
        int p2 = board[15];

        if (p1 > p2) return 1;
        if (p2 > p1) return 2;
        return 0;
    }
}