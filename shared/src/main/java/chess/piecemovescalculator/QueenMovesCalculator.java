package chess.piecemovescalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator {
    @Override
    public int[][] getMoveDirection(){
        return new int[][]{
                //horizontal & vertical movements
                {1, 0}, //moves down
                {0, -1}, // moves left
                {0, 1}, //moves right
                {-1, 0}, //moves up
                //diagonal movements:
                {-1, -1},
                {-1, 1},
                {1, -1},
                {1, 1}
        };
    }

    @Override
    public boolean hasMultipleMoves() {
        return true;
    }
}
