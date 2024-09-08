package chess.piecemovescalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator{
    @Override
    public int[][] getMoveDirection(){
        return new int[][]{
                {1, 0}, //moves down
                {0, -1}, // moves left
                {0, 1}, //moves right
                {-1, 0} //moves up
        };
    }

    @Override
    public boolean hasMultipleMoves() {
        return true;
    }
}
