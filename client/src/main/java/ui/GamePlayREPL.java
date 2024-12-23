package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import model.GameData;

import java.util.Scanner;

import static java.lang.System.out;

public class GamePlayREPL {
    ServerFacade server;
    ChessGame game;
    int gameID;
    public static ChessGame.TeamColor color;
    public static BoardPrint boardPrint;
    PostloginREPL postloginREPL;

    public GamePlayREPL(ServerFacade server, GameData gameData, ChessGame.TeamColor color) {
        this.server = server;
        this.game = gameData.game();
        this.color = color;
        this.gameID = gameData.gameID();
        this.boardPrint = new BoardPrint(game);
        this.postloginREPL = new PostloginREPL(server);

    }
    public GamePlayREPL(ServerFacade server, GameData gameData) {
        this(server, gameData, null);  // Or any default color, or null if acceptable
    }

    public void run(){
        boolean isInGame = true;
        while(isInGame){
            String[] input = getUserInput();
            switch(input[0]){
                case "help":
                    printHelpMenu();
                    break;
                case "quit":
                    return;
                case "leave":
                    isInGame = false;
                    server.leave(gameID);
                    break;
                case "redraw":
                    boardPrint.printBoard(color);
                    break;
                case "move":
                    handleMakeMove(input);
                    break;
                case "resign":
                    out.println("Are you sure you want to forfeit? (yes/no)");
                    String[] confirmation = getUserInput();
                    if (confirmation.length == 1 && confirmation[0].equalsIgnoreCase("yes")) {
                        server.resign(gameID);
                        isInGame = false;
                    }
                    else {
                        out.println("Resignation cancelled");
                    }
                    break;
                case "highlight":
                    if (input.length == 2 && input[1].matches("[a-h][1-8]")) {
                        ChessPosition position = new ChessPosition(input[1].charAt(1) - '0', input[1].charAt(0) - ('a'-1));
                        boardPrint.printBoard(color, position);
                    }
                    else {
                        out.println("Please provide a coordinate (ex: 'c3')");
                        printHighlight();
                    }
                    break;
                default:
                    out.println("Will implement more commands in phase 6 - for now just quit or leave");
                    break;
            }
        }
        if(!isInGame){
            postloginREPL.run();
        }
    }
    private void handleMakeMove(String[] input) {
        if (input.length >= 3 && input[1].matches("[a-h][1-8]") && input[2].matches("[a-h][1-8]")) {
            ChessPosition from = new ChessPosition((input[1].charAt(1) - '0'), input[1].charAt(0) - ('a' - 1));
            ChessPosition to = new ChessPosition((input[2].charAt(1) - '0'), input[2].charAt(0) - ('a' - 1));


            ChessPiece.PieceType promotion = null;
            if (input.length == 4) {
                promotion = getPieceType(input[3]);
                if (promotion == null) { // If it was improperly typed by the user
                    out.println("Please provide proper promotion piece name (ex: 'knight')");
                    printMakeMove();
                }
            }

            server.makeMove(gameID, new ChessMove(from, to, promotion));
        }
        else {
            out.println("Please provide a to and from coordinate (ex: 'c3 d5')");
            printMakeMove();
        }
    }
    public ChessPiece.PieceType getPieceType(String name) {
        return switch (name.toUpperCase()) {
            case "QUEEN" -> ChessPiece.PieceType.QUEEN;
            case "BISHOP" -> ChessPiece.PieceType.BISHOP;
            case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
            case "ROOK" -> ChessPiece.PieceType.ROOK;
            case "PAWN" -> ChessPiece.PieceType.PAWN;
            default -> null;
        };
    }

    private void printHelpMenu(){
        out.println("redraw - redraw the game board");
        out.println("leave - leave the current game");
        printMakeMove();
        out.println("resign - forfeit this game");
        printHighlight();
        out.println("help - show this menu");
    }

    private void printMakeMove() {
        out.println("move <from> <to> <promotion_piece> - make a move (Promotion piece should only be used when a move will promote a pawn)");
    }

    private void printHighlight() {
        out.println("highlight <coordinate> - highlight all legal moves for the given piece");
    }

    private String[] getUserInput() {
        out.print("\n[In-Game] >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }
}
