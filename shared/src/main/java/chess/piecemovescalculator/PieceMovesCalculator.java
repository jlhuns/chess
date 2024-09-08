package chess.piecemovescalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public interface PieceMovesCalculator {
    int[][] getMoveDirection();

    default boolean hasMultipleMoves() {
        return false;
    }

    default Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor teamColor = board.getPiece(position).getTeamColor();
        int row = position.getRow();
        int column = position.getColumn();

        // Loop over all directions
        for (int[] direction : getMoveDirection()) {
            int newRow = row;
            int newColumn = column;

            if (hasMultipleMoves()) {
                while (true) {
                    newRow += direction[0];
                    newColumn += direction[1];

                    // Break if the position is out of bounds
                    if (!isValidPosition(newRow, newColumn)) {
                        break;
                    }

                    ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                    ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                    // If the square is empty, add the move
                    if (pieceAtNewPosition == null) {
                        moves.add(new ChessMove(position, newPosition, null));
                    } else {
                        // If the square contains an opponent's piece, capture it and stop
                        if (pieceAtNewPosition.getTeamColor() != teamColor) {
                            moves.add(new ChessMove(position, newPosition, null));
                        }
                        // Stop sliding in this direction after encountering a piece
                        break;
                    }
                }
            }else{
                newRow = row + direction[0];
                newColumn = column + direction[1];

                if (isValidPosition(newRow, newColumn)) {
                    ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                    ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                    if (pieceAtNewPosition == null) {
                        moves.add(new ChessMove(position, newPosition, null));
                    } else if (pieceAtNewPosition.getTeamColor() != teamColor) {
                        moves.add(new ChessMove(position, newPosition, null));
                    }
                }
            }
        }

        return moves;
    }

    default boolean isValidPosition(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

}
