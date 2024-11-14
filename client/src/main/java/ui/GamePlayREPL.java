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

    public GamePlayREPL(ServerFacade server, GameData gameData, ChessGame.TeamColor color) {
        this.server = server;
        this.game = gameData.game();
        this.color = color;
        this.gameID = gameData.gameID();
        this.boardPrint = new BoardPrint(game);
    }

    public void run(){
        boolean isInGame = true;
        if(color == ChessGame.TeamColor.WHITE){
            boardPrint.printBoard(ChessGame.TeamColor.WHITE);
        }else if(color == ChessGame.TeamColor.BLACK){
            boardPrint.printBoard(ChessGame.TeamColor.BLACK);
        }else{
            boardPrint.printBoard(ChessGame.TeamColor.WHITE);
            boardPrint.printBoard(ChessGame.TeamColor.BLACK);
        }
        while(isInGame){
            String[] input = getUserInput();
            switch(input[0]){
                case "quit":
                    return;
                default:
                    out.println("Will implement more commands in phase 6 - for now just quit");
                    break;
            }
        }
    }

    private String[] getUserInput() {
        out.print("\n[In-Game] >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }
}
