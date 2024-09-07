package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMoveCalculator implements PieceMovesCalculator {
    private final ChessPiece piece;

    public PawnMoveCalculator(ChessPiece piece) {
        this.piece = piece;
    }

    private boolean isPromotionRow(int row) {
        return (piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 8) ||
                (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 1);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        List<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int column = position.getColumn();
        ChessGame.TeamColor color = piece.getTeamColor();

        // White moves up, black moves down
        int moveDirection = color == ChessGame.TeamColor.WHITE ? 1 : -1;

        // Single square move
        ChessPosition oneStepForward = new ChessPosition(row + moveDirection, column);
        if (isValidPosition(oneStepForward.getRow(), oneStepForward.getColumn()) && board.getPiece(oneStepForward) == null && !isPromotionRow(row + moveDirection)) {
            moves.add(new ChessMove(position, oneStepForward, null));
        }

        // Double square move
        int startRow = color == ChessGame.TeamColor.WHITE ? 2 : 7;
        if (row == startRow && board.getPiece(oneStepForward) == null) {
            ChessPosition twoStepForward = new ChessPosition(row + (2 * moveDirection), column);
            if (isValidPosition(twoStepForward.getRow(), twoStepForward.getColumn()) && board.getPiece(twoStepForward) == null && !isPromotionRow(row + moveDirection)) {
                moves.add(new ChessMove(position, twoStepForward, null));
            }
        }

        // Diagonal capture
        int[][] diagonalCaptureDirection = {
                {moveDirection, -1}, // capture left
                {moveDirection, 1}   // capture right
        };
        for (int[] diagonal : diagonalCaptureDirection) {
            int newRow = row + diagonal[0];
            int newColumn = column + diagonal[1];
            if (isValidPosition(newRow, newColumn)) {
                ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                ChessPiece capturePiece = board.getPiece(newPosition);
                if (capturePiece != null && capturePiece.getTeamColor() != color) {
                    if(row == (color == ChessGame.TeamColor.WHITE ? 7 : 2)){
                        moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                    }else{
                        moves.add(new ChessMove(position, newPosition, null));
                    }

                }
            }
        }

        // Promotion
        if (row == (color == ChessGame.TeamColor.WHITE ? 7 : 2)) {
            // Promotion to Queen, Rook, Bishop, Knight
            ChessPosition promotionPosition = new ChessPosition(row + moveDirection, column);
            if (isValidPosition(promotionPosition.getRow(), promotionPosition.getColumn())) {
                // Add promotion moves to all four types
                moves.add(new ChessMove(position, promotionPosition, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, promotionPosition, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(position, promotionPosition, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(position, promotionPosition, ChessPiece.PieceType.KNIGHT));
            }
        }

        return moves;
    }
}
