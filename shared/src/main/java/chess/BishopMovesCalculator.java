package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMovesCalculator implements PieceMovesCalculator{
    private final ChessPiece piece;

    public BishopMovesCalculator(ChessPiece piece) {
        this.piece = piece;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        ChessGame.TeamColor teamColor = piece.getTeamColor();

        int row = position.getRow();
        int column = position.getColumn();

        int[][] moveDirection = {
                {-1,-1},
                {-1,1},
                {1,-1},
                {1,1},
        };

        for(int[] direction : moveDirection) {
            int newRow = row;
            int newColumn = column;

            while (true) {
                newRow += direction[0];
                newColumn += direction[1];

                if (!isValidPosition(newRow, newColumn)) {
                    break; // Out of bounds
                }

                ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null) {
                    // Add move to an empty square
                    moves.add(new ChessMove(position, newPosition, null));
                } else if (pieceAtNewPosition.getTeamColor() != teamColor) {
                    // Capture an opponent's piece and stop further movement in this direction
                    moves.add(new ChessMove(position, newPosition, null));
                    break;
                } else {
                    // Stop if it's your own piece
                    break;
                }
            }
        }
        return moves;
    }
}
