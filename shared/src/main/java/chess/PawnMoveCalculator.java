package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMoveCalculator implements PieceMovesCalculator{
    private final ChessPiece piece;

    public PawnMoveCalculator(ChessPiece piece) {
        this.piece = piece;
    }
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        List<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int column = position.getColumn();
        ChessGame.TeamColor color = piece.getTeamColor();

        //white moves up, black moves down
        int moveDirection = color == ChessGame.TeamColor.WHITE ? 1 : -1;

        //single square move
        ChessPosition oneStepForward = new ChessPosition(row + moveDirection, column);
        if (isValidPosition(oneStepForward.getRow(), oneStepForward.getColumn()) && board.getPiece(oneStepForward) == null) {
            moves.add(new ChessMove(position, oneStepForward, ChessPiece.PieceType.PAWN));
        }

        //double square move
        int startPosition = color == ChessGame.TeamColor.WHITE ? 2 : -7;
        if(row == startPosition && column == startPosition) {
            ChessPosition twoStepForward = new ChessPosition(row + (2* moveDirection), column);
            if (isValidPosition(twoStepForward.getRow(), twoStepForward.getColumn()) && board.getPiece(twoStepForward) == null) {
                moves.add(new ChessMove(position, twoStepForward, ChessPiece.PieceType.PAWN));
            }
        }

        //diagonal capture
        int[][] diagonalCaptureDirection = {
                {moveDirection, -1}, //capture left
                {moveDirection, 1} //capture right
        };
        for(int[] diagonal : diagonalCaptureDirection){
            int newRow = row + diagonal[0];
            int newColumn = column + diagonal[1];
            if(isValidPosition(newRow, newColumn)) {
                ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                ChessPiece capturePiece = board.getPiece(newPosition);
                if(capturePiece != null && capturePiece.getTeamColor() != color) {
                    moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.PAWN));
                }
            }
        }

        return moves;
    }
}
