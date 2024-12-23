package chess;
import java.util.*;


/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final Map<ChessPosition, ChessPiece> board;

    public ChessBoard() {
        board = new HashMap<ChessPosition, ChessPiece>();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board.put(position, piece);
    }

    public void removePiece(ChessPosition position) {
        board.remove(position);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {return board.get(position);
    }

    public ChessPosition getPieceLocation(ChessPiece.PieceType piece, ChessGame.TeamColor teamColor) {
        for(Map.Entry<ChessPosition, ChessPiece> entry : board.entrySet()) {
            ChessPosition position = entry.getKey();
            ChessPiece pieceToCheck = entry.getValue();
            if(pieceToCheck.pieceType.equals(piece) && pieceToCheck.teamColor == teamColor) {
                return position;
            }
        }
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board.clear();

        // White pieces
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(2, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        // Black pieces
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));

        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(7, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }
    public ChessBoard cloneBoard(){
        ChessBoard newBoard = new ChessBoard();
        for(Map.Entry<ChessPosition, ChessPiece> entry : board.entrySet()){
            ChessPosition position = entry.getKey();
            ChessPiece piece = entry.getValue();
            newBoard.addPiece(position, piece);
        }
        return newBoard;
    }

    public Map<ChessPosition, ChessPiece> getTeamPiecesOnBoard(ChessGame.TeamColor teamColor){
        Map<ChessPosition, ChessPiece> teamPieces = new HashMap<>();
        for(Map.Entry<ChessPosition, ChessPiece> entry : board.entrySet()){
            ChessPosition position = entry.getKey();
            ChessPiece piece = entry.getValue();
            if(piece.getTeamColor() == teamColor){
                teamPieces.put(position, piece);
            }
        }
        return teamPieces;
    }

    /**
     * Moves a piece, ignoring all status checks and invalid moves
     */
    public void forceMove(ChessMove move){
        // Get the piece that is making the move
        ChessPiece movingPiece = getPiece(move.startPosition);

        // Remove the piece from its current position
        removePiece(move.startPosition);

        // If there's a piece at the destination (capture), remove it
        if (getPiece(move.endPosition) != null) {
            removePiece(move.endPosition);
        }

        //if there is a promotion reflect that
        if(move.pieceType != null){
            movingPiece.pieceType = move.pieceType;
        }
        // Place the moving piece at the destination position
        addPiece(move.endPosition, movingPiece);
    }

    public Map<ChessPosition, ChessPiece> getBoard(){
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + board +
                '}';
    }
}
