package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamColor;
    public ChessGame() {
        this.teamColor = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        if(piece == null){
            return null;
        }else{
            Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);
            for(ChessMove move : allMoves){
                ChessBoard simBoard = board.cloneBoard();
                simBoard.forceMove(move);

                if(!isInCheckAfterMove(piece.teamColor, simBoard)){
                    validMoves.add(move);
                }
            }
            return validMoves;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.startPosition);
        ChessGame.TeamColor teamColor = piece.teamColor;
        if(piece == null || this.teamColor != teamColor){
            throw new InvalidMoveException();
        }
        Collection<ChessMove> validMoves = validMoves(move.startPosition);
        if(validMoves.contains(move)){
            board.forceMove(move);
        }else{
            throw new InvalidMoveException();
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingLocation = board.getPieceLocation(ChessPiece.PieceType.KING, teamColor);
        Set<ChessPosition> threatenedPositions = getAllOpponentThreatenedPositions(teamColor, board);
        if(threatenedPositions.contains(kingLocation)){
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    //currently the problem is that pawns don't thraten a square unless the king is in that diagonal square. I will need to fix this by maybe
    //using clones and doing possible scenerios. might be able to use isincheck to help out. i.e move king and if still in check then invalid move. test all
    //of king moves that way. then try to block or take a piece if able using a similar approach. just need to find way to clone board.
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingLocation = board.getPieceLocation(ChessPiece.PieceType.KING, teamColor);
        ChessPiece kingPiece = board.getPiece(kingLocation);

        if (!isInCheck(teamColor)) {
            return false;
        }

        Collection<ChessMove> kingMoves = kingPiece.pieceMoves(board, kingLocation);
        //check all king moves
        for (ChessMove move : kingMoves) {
            ChessBoard simBoard = board.cloneBoard();
            simBoard.forceMove(move);  // Simulate the move on the cloned board
            if (!isInCheckAfterMove(teamColor, simBoard)) {
                return false;  // If the king can escape check, it's not checkmate
            }
        }

        // Check if any other piece can block the check or capture the attacking piece
        Map<ChessPosition, ChessPiece> teamPieces = board.getTeamPiecesOnBoard(teamColor);
        for (Map.Entry<ChessPosition, ChessPiece> entry : teamPieces.entrySet()) {
            ChessPosition position = entry.getKey();
            ChessPiece piece = entry.getValue();

            // Get all possible moves for each piece
            Collection<ChessMove> pieceMoves = piece.pieceMoves(board, position);
            for (ChessMove move : pieceMoves) {
                ChessBoard simBoard = board.cloneBoard();
                simBoard.forceMove(move);  // Simulate the piece move
                if (!isInCheckAfterMove(teamColor, simBoard)) {
                    return false;  // If any move resolves the check, it's not checkmate
                }
            }
        }

        // If no valid move prevents check, it's checkmate
        return true;
    }

    private boolean isInCheckAfterMove(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingLocation = board.getPieceLocation(ChessPiece.PieceType.KING, teamColor);
        Set<ChessPosition> threatenedPositions = getAllOpponentThreatenedPositions(teamColor, board);
        if (threatenedPositions.contains(kingLocation)) {
            return true;
        }
        return false;
    }

    private Set<ChessPosition> getAllOpponentThreatenedPositions(ChessGame.TeamColor teamcolor, ChessBoard board){
        Set<ChessPosition> threatenedPositions = new HashSet<>();
        Map<ChessPosition, ChessPiece> opponentPieces = board.getTeamPiecesOnBoard(
                teamcolor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE
        );

        for (Map.Entry<ChessPosition, ChessPiece> entry : opponentPieces.entrySet()) {
            ChessPiece piece = entry.getValue();
            ChessPosition position = entry.getKey();
            Collection<ChessMove> moves = piece.pieceMoves(board, position);
            for (ChessMove move : moves) {
                threatenedPositions.add(move.endPosition);
            }
        }
        return threatenedPositions;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //check if each move is valid, if there are no valid moves and not in check, its a stalemate
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
