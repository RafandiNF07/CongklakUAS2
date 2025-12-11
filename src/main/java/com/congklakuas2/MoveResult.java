package com.congklakuas2;

import java.util.ArrayList;
import java.util.List;

public class MoveResult {
    public boolean valid = true;
    public boolean freeTurn = false;

    public int startPit;    
    public int startSeeds;
    public List<Integer> path = new ArrayList<>();

    public MoveResult() {}

    public MoveResult(int pit, int seeds) {
        this.startPit = pit;
        this.startSeeds = seeds;
    }
}
