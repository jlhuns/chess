package ui;

import client.ServerFacade;

import java.util.Scanner;
import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class PreloginREPL {
    ServerFacade server;
    public PreloginREPL(ServerFacade server) {
        this.server = server;
//      postloginREPL = new PostloginREPL(server);
    }

    public void run(){
        boolean isLoggedIn = false;
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        out.println("Welcome to Chess! Enter 'help' to get started.");
        while(!isLoggedIn){
            String[] input = getUserInput();
            switch(input[0]){
                case "quit": ;
                    return;
                case "help":
                    printHelpMenu();
                    break;
                case "login":
                    isLoggedIn = true;
                    break;
                case "register":
                    isLoggedIn = true;
                    break;
                default:
                    out.println("Command not recognized, please try again");
                    printHelpMenu();
                    break;
            }
        }
    }


    private String[] getUserInput() {
        out.print("\n[LOGGED OUT] >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }

    private void printHelpMenu() {
        printRegister();
        printLogin();
        out.println("quit - stop playing");
        out.println("help - show this menu");
    }

    private void printRegister() {
        out.println("register <USERNAME> <PASSWORD> <EMAIL> - create a new user");
    }

    private void printLogin() {
        out.println("login <USERNAME> <PASSWORD> - login to an existing user");
    }
}
