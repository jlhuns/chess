package ui;

import client.ServerFacade;

import java.util.Scanner;

import static java.lang.System.out;

public class PostloginREPL {
    ServerFacade server;
    public PostloginREPL(ServerFacade server) {
        this.server = server;
    }

    public void run(){
        boolean isLoggedIn = true;
        while(isLoggedIn) {
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
                    int gameID = server.createGame(input[1]);
                    if(gameID >= 0){
                        out.println("Game created successfully at gameId: " + gameID);
                    }else{
                        out.println("Game creation failed");
                    }

            }
        }
        if(!isLoggedIn) {
            PreloginREPL prelogin = new PreloginREPL(server);
            prelogin.run();
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
}
