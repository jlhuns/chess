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

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>(PieceMovesCalculator.super.pieceMoves(board, position));

        //castling logic
        ChessPiece piece = board.getPiece(position);
        ChessGame.TeamColor teamColor = piece.getTeamColor();

        if(!piece.hasMoved()){
            if(canCastleKingside(board, piece.getTeamColor())){
                ChessMove castleMove = new ChessMove(position, new ChessPosition(position.getRow(), position.getColumn() + 2), null);
                // Rook's move for kingside castling
                ChessPosition rookStartPosition = new ChessPosition(position.getRow(), 8);
                ChessPosition rookEndPosition = new ChessPosition(position.getRow(), position.getColumn() + 1);

                castleMove.setCastleMove(rookStartPosition, rookEndPosition);
                moves.add(castleMove);
            }
            if(canCastleQueenside(board, piece.getTeamColor())){
                ChessMove castleMove = new ChessMove(position, new ChessPosition(position.getRow(), position.getColumn() - 2), null);

                ChessPosition rookStartPosition = new ChessPosition(position.getRow(), 1);
                ChessPosition rookEndPosition = new ChessPosition(position.getRow(), position.getColumn() - 1);

                castleMove.setCastleMove(rookStartPosition, rookEndPosition);
                moves.add(castleMove);
            }
        }
        return moves;
    }

    private boolean canCastleKingside(ChessBoard board, ChessGame.TeamColor teamColor) {
        // Check if the squares between the king and rook are empty
        int row = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : 8;
        return board.isEmpty(new ChessPosition(row, 6)) && board.isEmpty(new ChessPosition(row, 7))
                && !board.getPiece(new ChessPosition(row, 8)).hasMoved();
    }

    private boolean canCastleQueenside(ChessBoard board, ChessGame.TeamColor teamColor) {
        // Check if the squares between the king and rook are empty
        int row = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : 8;
        return board.isEmpty(new ChessPosition(row, 2)) && board.isEmpty(new ChessPosition(row, 3)) && board.isEmpty(new ChessPosition(row, 4))
                && !board.getPiece(new ChessPosition(row, 1)).hasMoved();
    }
}

