package chess.piecemovescalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMoveCalculator implements PieceMovesCalculator {
    private final ChessGame.TeamColor teamColor;
    private final int startRow;
    private final int moveDirection;
    private final int promotionRow;
    private final int[][] diagonalMoveDirection;

    public PawnMoveCalculator(ChessPiece piece) {
        this.teamColor = piece.getTeamColor();

        if (teamColor == ChessGame.TeamColor.WHITE){
            this.startRow = 2;
            this.promotionRow = 8;
            this.moveDirection = 1;
            this.diagonalMoveDirection = new int[][]{
                    {1,-1},
                    {1, 1}
            };

        }else{
            this.startRow = 7;
            this.promotionRow = 1;
            this.moveDirection = -1;
            this.diagonalMoveDirection = new int[][]{
                    {-1,-1},
                    {-1,1}
            };

        }
    }

    private boolean canPromote(ChessPosition newPosition){
        return newPosition.getRow() == promotionRow;
    }

    @Override
    public int[][] getMoveDirection(){
        return new int[][]{};
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        List<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int column = position.getColumn();

        //one square move
        ChessPosition oneSquareMove = new ChessPosition(row + moveDirection, column);
        ChessPiece pieceAtNewPosition = board.getPiece(oneSquareMove);
        if (pieceAtNewPosition == null) {
            if (canPromote(oneSquareMove)) {
                // Promotion to Queen, Rook, Bishop, Knight
                moves.add(new ChessMove(position, oneSquareMove, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, oneSquareMove, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(position, oneSquareMove, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(position, oneSquareMove, ChessPiece.PieceType.KNIGHT));
            } else {
                // Regular move
                moves.add(new ChessMove(position, oneSquareMove, null));
            }
        }
        //two square move
        if(row == startRow) {
            ChessPosition twoSquareMove = new ChessPosition(row + (2 * moveDirection), column);
            ChessPiece pieceAtNewPosition2 = board.getPiece(twoSquareMove);
            if (pieceAtNewPosition2 == null && pieceAtNewPosition == null) {
                moves.add(new ChessMove(position, twoSquareMove, null));
            }
        }
        //diagonal capture
        for (int[] diagonalDirection : diagonalMoveDirection){
            int newRow = row + diagonalDirection[0];
            int newColumn = column + diagonalDirection[1];
            if(isValidPosition(newRow, newColumn)){
                ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                ChessPiece newPieceAtNewPosition = board.getPiece(newPosition);
                if(newPieceAtNewPosition != null && newPieceAtNewPosition.getTeamColor() != teamColor){
                    if (canPromote(newPosition)) {
                        moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
                    }else{
                        moves.add(new ChessMove(position, newPosition, null));
                    }
                }
            }
        }

        return moves;
    }
}
