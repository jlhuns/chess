package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class RookMovesCalculator implements PieceMovesCalculator{
    private final ChessPiece piece;

    public RookMovesCalculator(ChessPiece piece) {
        this.piece = piece;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection <ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int column = position.getColumn();
        ChessGame.TeamColor teamColor = piece.getTeamColor();

        int[][] moveDirection = {
                {1, 0}, //moves down
                {0, -1}, // moves left
                {0, 1}, //moves right
                {-1, 0} //moves up
        };

        for(int[] direction : moveDirection){
            int newRow = row;
            int newColumn = column;

            while(true){
                newRow += direction[0];
                newColumn += direction[1];

                if(!isValidPosition(newRow, newColumn)){
                    break;
                }

                ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if(pieceAtNewPosition == null){
                    moves.add(new ChessMove(position, newPosition, null));
                } else if (pieceAtNewPosition.getTeamColor() != teamColor) {
                    moves.add(new ChessMove(position, newPosition, null));
                    break;
                }else{
                    break;
                }

            }
        }
        return moves;
    }
}
