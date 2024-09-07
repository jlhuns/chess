package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMovesCalculator implements PieceMovesCalculator {
    private final ChessPiece piece;

    public KingMovesCalculator(ChessPiece piece) {
        this.piece = piece;
    }
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        List<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int column = position.getColumn();
        ChessGame.TeamColor teamColor = piece.getTeamColor();

        // Define possible directions the King can move
        int[][] movementDirections = {
                {1, 0},  // Move down
                {-1, 0}, // Move up
                {0, 1},  // Move right
                {0, -1}, // Move left
                {1, 1},  // Move down-right
                {-1, -1},// Move up-left
                {1, -1}, // Move down-left
                {-1, 1}  // Move up-right
        };
        //check all positions
        for (int[] direction : movementDirections) {
            int newRow = row + direction[0];
            int newColumn = column + direction[1];
            if (isValidPosition(newRow, newColumn)) {
                ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.getTeamColor() != teamColor) {
                    moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KING));
                }
            }
        }
        return moves;
    }
}
