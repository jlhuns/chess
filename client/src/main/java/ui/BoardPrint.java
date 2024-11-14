package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class BoardPrint {
    ChessGame game;

    public BoardPrint(ChessGame game) {
        this.game = game;
    }

    public void updateGame(ChessGame game) {
        this.game = game;
    }
    public void printBoard(ChessGame.TeamColor color) {
        StringBuilder output = new StringBuilder();
        output.append(SET_TEXT_BOLD);

        // Determine board orientation based on team color
        int startRow = color == ChessGame.TeamColor.WHITE ? 0 : 9;
        int rowIncrement = color == ChessGame.TeamColor.WHITE ? 1 : -1;

        for (int i = 0; i <= 9; i++) {
            int row = startRow + i * rowIncrement;
            for (int col = 0; col <= 9; col++) {
                ChessPiece chessPiece = game.getBoard().getPiece(new ChessPosition(row, col));
                String bgColor = (row + col) % 2 == 0 ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                output.append(bgColor); // Set the background color
                output.append(chessPiece != null ? getPieceSymbol(chessPiece) : EMPTY);
                output.append(RESET_BG_COLOR);
            }
            output.append("\n");
        }

        output.append(RESET_TEXT_BOLD_FAINT);  // Reset formatting at the end
        System.out.print(output);
    }

    private String getPieceSymbol(ChessPiece chessPiece) {
        if (chessPiece == null) {
            return EMPTY;
        }
        ChessGame.TeamColor color = chessPiece.getTeamColor();
        ChessPiece.PieceType type = chessPiece.getPieceType();
        switch (type) {
            case KING: return color == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            case QUEEN: return color == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP: return color == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT: return color == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK: return color == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            case PAWN: return color == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
            default: return EMPTY;
        }
    }
}