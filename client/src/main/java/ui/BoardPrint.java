package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static java.lang.Math.abs;
import static java.lang.System.out;
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
        String[] letters = new String[]{"0", "a", "b", "c", "d", "e", "f", "g", "h", "0"};

        // Determine board orientation based on team color
        int startRow = color == ChessGame.TeamColor.WHITE ? 8 : 1;
        int rowIncrement = color == ChessGame.TeamColor.WHITE ? -1 : 1;

        for (int i = 0; i <= 9; i++) {
            int row = startRow + (i - 1) * rowIncrement;
            for (int col = 0; col <= 9; col++) {
                // Set background color for edges (outside the chessboard)
                if (col == 0 || col == 9) {
                    output.append(SET_BG_COLOR_BLACK);
                    if (i == 0 || i == 9) {
                        // corners
                        output.append(EMPTY);
                    } else {
                        // numbers on the edges
                        output.append(" ").append(abs(row)).append(EM_SPACE);
                    }
                }
                // Set letters for the top and bottom borders
                else if (i == 0 || i == 9) {
                    output.append(SET_BG_COLOR_BLACK);
                    output.append(" ").append(letters[col]).append(EM_SPACE); // From a-h
                }
                // Set grey backgrounds for the chessboard squares
                else {
                    String bgColor = (row + col) % 2 == 0 ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                    output.append(bgColor); // Set the background color for chessboard squares
                    ChessPiece chessPiece = game.getBoard().getPiece(new ChessPosition(row, col));
                    output.append(chessPiece != null ? getPieceSymbol(chessPiece) : EMPTY);
                    output.append(RESET_BG_COLOR); // Reset the background color
                }

                output.append(RESET_BG_COLOR); // Reset background color after every cell
            }
            output.append("\n");
        }

        output.append(RESET_TEXT_BOLD_FAINT); // Reset formatting at the end
        out.print(output);
    }

    private String getPieceSymbol(ChessPiece chessPiece) {
        if (chessPiece == null) {
            return EMPTY;
        }
        ChessGame.TeamColor color = chessPiece.getTeamColor();
        ChessPiece.PieceType type = chessPiece.getPieceType();
        switch (type) {
            case KING:
                return color == ChessGame.TeamColor.WHITE ?  BLACK_KING : WHITE_KING;
            case QUEEN:
                return color == ChessGame.TeamColor.WHITE ? BLACK_QUEEN : WHITE_QUEEN;
            case BISHOP:
                return color == ChessGame.TeamColor.WHITE ? BLACK_BISHOP : WHITE_BISHOP;
            case KNIGHT:
                return color == ChessGame.TeamColor.WHITE ?  BLACK_KNIGHT : WHITE_KNIGHT;
            case ROOK:
                return color == ChessGame.TeamColor.WHITE ?  BLACK_ROOK : WHITE_ROOK;
            case PAWN:
                return color == ChessGame.TeamColor.WHITE ?  BLACK_PAWN : WHITE_PAWN;
            default:
                return EMPTY;
        }
    }
}
