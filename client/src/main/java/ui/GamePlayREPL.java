package ui;

import chess.ChessGame;
import client.ServerFacade;
import model.GameData;

import java.util.Scanner;

import static java.lang.System.out;

public class GamePlayREPL {
    ServerFacade server;
    ChessGame game;
    int gameID;
    public static ChessGame.TeamColor color;
    public BoardPrint boardPrint;
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
        if(color == ChessGame.TeamColor.WHITE){
            boardPrint.printBoard(ChessGame.TeamColor.WHITE);
        }else if(color == ChessGame.TeamColor.BLACK){
            boardPrint.printBoard(ChessGame.TeamColor.BLACK);
        }else{
            boardPrint.printBoard(ChessGame.TeamColor.WHITE);
            out.println("\n");
            boardPrint.printBoard(ChessGame.TeamColor.BLACK);
        }
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
                case "Redraw":
                    break;
                case "Make Move":
                    break;
                case "Resign":
                    out.println("Are you sure you want to forfeit? (yes/no)");
                    String[] confirmation = getUserInput();
                    if (confirmation.length == 1 && confirmation[0].equalsIgnoreCase("yes")) {
                        server.resign(gameID);
                    }
                    else {
                        out.println("Resignation cancelled");
                    }
                    break;
                case "Highlight":
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
    public BoardPrint getBoardPrint() {
        return boardPrint;
    }
    public ChessGame.TeamColor getColor() {
        return color;
    }
}
