import client.ServerFacade;
import ui.PreloginREPL;

public class Main {
    public static void main(String[] args) throws Exception {
        ServerFacade server = new ServerFacade();

        PreloginREPL prelogin = new PreloginREPL(server);
        prelogin.run();

        System.out.println("Exited");
    }
}


