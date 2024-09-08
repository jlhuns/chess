package chess.piecemovescalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMovesCalculator implements PieceMovesCalculator {
    @Override
    public int[][] getMoveDirection(){
        return new int[][]{
                {1, 0},  // Move down
                {-1, 0}, // Move up
                {0, 1},  // Move right
                {0, -1}, // Move left
                {1, 1},  // Move down-right
                {-1, -1},// Move up-left
                {1, -1}, // Move down-left
                {-1, 1}  // Move up-right
        };
    }
}
