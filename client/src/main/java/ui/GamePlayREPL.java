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
    BoardPrint boardPrint;
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
                case "quit":
                    return;
                case "leave":
                    isInGame = false;
                    break;
                default:
                    out.println("Will implement more commands in phase 6 - for now just quit");
                    break;
            }
        }
        if(!isInGame){
            postloginREPL.run();
        }
    }

    private String[] getUserInput() {
        out.print("\n[In-Game] >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }
}
