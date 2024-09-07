package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueenMovesCalculator implements PieceMovesCalculator {
    private final ChessPiece piece;

    public QueenMovesCalculator(ChessPiece piece) {
        this.piece = piece;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor teamColor = piece.getTeamColor();

        int row = position.getRow();
        int column = position.getColumn();

        int[][] moveDirections = {
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

        for(int[] moveDirection : moveDirections) {
            int newRow = row;
            int newColumn = column;

            while(true){
                newRow += moveDirection[0];
                newColumn += moveDirection[1];

                if(!isValidPosition(newRow, newColumn)){
                    break;
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
