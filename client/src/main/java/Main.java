import chess.ChessGame;
import client.ServerFacade;
import ui.BoardPrint;
import ui.PreloginREPL;
import ui.BoardPrint.*;

public class Main {
    public static void main(String[] args) throws Exception {
        ServerFacade server = new ServerFacade();
        ChessGame game = new ChessGame();
        BoardPrint printer = new BoardPrint(game);
        printer.printBoard(ChessGame.TeamColor.BLACK);

        PreloginREPL prelogin = new PreloginREPL(server);
        prelogin.run();

        System.out.println("Exited");

    }
}


