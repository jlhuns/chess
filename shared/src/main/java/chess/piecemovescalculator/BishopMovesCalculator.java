package chess.piecemovescalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator{
    @Override
    public int[][] getMoveDirection(){
        return new int[][]{
                {-1,-1},
                {-1,1},
                {1,-1},
                {1,1},
        };
    }
    @Override
    public boolean hasMultipleMoves(){
        return true;
    }
}
