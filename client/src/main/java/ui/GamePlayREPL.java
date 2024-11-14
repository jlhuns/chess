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

    public GamePlayREPL(ServerFacade server, GameData gameData, ChessGame.TeamColor color) {
        this.server = server;
        this.game = gameData.game();
        this.color = color;
        this.gameID = gameData.gameID();
    }

    public void run(){
        boolean isInGame = true;
        while(isInGame){
            String[] input = getUserInput();
            switch(input[0]){
                case "quit":
                    return;
                default:
                    out.println("Will implement in phase 6");
                    return;
            }
        }
    }

    private String[] getUserInput() {
        out.print("\n[In-Game] >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }
}
