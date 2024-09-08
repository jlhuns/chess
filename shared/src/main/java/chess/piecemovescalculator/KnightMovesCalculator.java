package chess.piecemovescalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator{
    @Override
    public int[][] getMoveDirection(){
        return new int[][] {
                {-1, 2},
                {1, 2},
                {-2, 1},
                {2, 1},
                {2, -1},
                {1, -2},
                {-2, -1},
                {-1, -2}
        };
    }
}
