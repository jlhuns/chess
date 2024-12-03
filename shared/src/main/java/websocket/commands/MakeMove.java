package websocket.commands;
import chess.ChessMove;

/**
 * Represents a command to make a move in a chess game.
 */
public class MakeMove extends UserGameCommand {

    private final ChessMove move;  // The chess move to be made

    public MakeMove(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }
}