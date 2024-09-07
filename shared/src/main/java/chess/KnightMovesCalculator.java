package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMovesCalculator implements PieceMovesCalculator{
    private final ChessPiece piece;

    public KnightMovesCalculator(ChessPiece piece) {
        this.piece = piece;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        ChessGame.TeamColor teamColor = piece.getTeamColor();
        int row = position.getRow();
        int column = position.getColumn();

        int[][] moveDirections = {
                {-1,2},
                {1,2},
                {-2,1},
                {2,1},
                {2,-1},
                {1,-2},
                {-2,-1},
                {-1,-2}
        };

        for(int[] direction : moveDirections) {
            int newRow = row + direction[0];
            int newColumn = column + direction[1];

            if (isValidPosition(newRow, newColumn)) {
                ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null) {
                    moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                } else if (pieceAtNewPosition.getTeamColor() != teamColor) {
                    moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                }
            }
        }
        return moves;
    }
}
