package ui;

import chess.ChessGame;
import client.ServerFacade;
import model.GameData;
import model.ListGamesResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.out;

public class PostloginREPL {
    ServerFacade server;
    List<GameData> games;
    boolean isInGame = false;
    public PostloginREPL(ServerFacade server) {
        this.server = server;
    }

    public void run(){
        boolean isLoggedIn = true;
        while(isLoggedIn & !isInGame) {
            String[] input = getUserInput();
            switch(input[0]){
                case "quit":
                    return;
                case "help":
                    printHelpMenu();
                    break;
                case "logout":
                    if(server.logout()) {
                        out.println("Logged out successfully");
                        isLoggedIn = false;
                        break;
                    }
                    break;
                case "create":
                    if (input.length != 2) {
                        out.println("Please provide a name");
                        printCreateGame();
                        break;
                    }
                    server.createGame(input[1]);
                    out.printf("Created game: %s%n", input[1]);
                    break;
                case "list":
                    getGames();
                    printGames();
                    break;
                case "join":
                    joinHandler(input, false);
                    break;
                case "observe":
                    joinHandler(input, true);
                    break;
                default:
                    out.println("Command not recognized, please try again");
                    printHelpMenu();
                    break;
            }
        }
        if(!isLoggedIn) {
            PreloginREPL prelogin = new PreloginREPL(server);
            prelogin.run();
        }
    }
    private void joinHandler(String[] input, boolean observer){
        if(observer) {
            if(input.length != 2 || !input[1].matches("\\d")) {
                out.println("Please provide a game ID");
                printJoinGame();
                return;
            }
        }else{
            if(input.length != 3 || !input[1].matches("\\d") || !input[2].toUpperCase().matches("WHITE|BLACK")) {
                out.println("Please provide a game ID and color choice");
                printJoinGame();
                return;
            }
        }
        int gameNumber = Integer.parseInt(input[1]);
        if(games.isEmpty() || games.size() <= gameNumber) {
            getGames();
            if (games.isEmpty()) {
                out.println("Error: Create a game first");
                return;
            }
            if (games.size() <= gameNumber){
                out.println("Error: Game ID does not exist");
                printGames();
                return;
            }
        }
        String color = null;
        GameData joinGame = games.get(gameNumber);
        if(!observer){
            color = input[2].toUpperCase();
        }

        ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf(color);
        //join game api handler
        if(server.joinGame(joinGame.gameID(), color)){
            out.println("You have joined the game");
            isInGame = true;
//            server, joinGame, color
            GamePlayREPL gameplayREPL = new GamePlayREPL(server, joinGame, teamColor);
            gameplayREPL.run();
        } else {
            out.println("Game does not exist or color taken");
            printJoinGame();
        }
    }

    private void printHelpMenu() {
        printCreateGame();
        printListGames();
        printJoinGame();
        printObserve();
        printLogout();
        out.println("quit - playing chess");
        out.println("help - with possible commands");
    }


    private String[] getUserInput() {
        out.print("\n[LOGGED IN] >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }

    private void printCreateGame(){
        out.println("create <NAME> - a game");
    }
    private void printLogout(){
        out.println("logout - when you are done");
    }
    private void printListGames(){
        out.println("list - games");
    }
    private void printJoinGame(){
        out.println("join <ID> [WHITE|BLACK]");
    }
    private void printObserve(){
        out.println("observe <ID> - a game");
    }

    private void getGames(){
        games = server.listGames();
    }
    private void printGames(){
        if(games == null || games.size() <= 0) {
            out.println("No games to join");
            return;
        }
        for(int i = 0; i < games.size(); i++){
            GameData game = games.get(i);
            String whiteUser = game.whiteUsername() != null? game.whiteUsername(): "open";
            String blackUser = game.blackUsername() != null? game.blackUsername(): "open";
            out.printf("%d -- Game Name: %s  |  White User: %s  |  Black User: %s %n", i, game.gameName(), whiteUser, blackUser);
        }
    }
}
