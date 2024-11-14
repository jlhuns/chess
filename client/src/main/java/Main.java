import client.ServerFacade;
import spark.Spark;
import ui.PreloginREPL;


import static java.lang.System.out;

public class Main {
    public static void main(String[] args) throws Exception {
        ServerFacade server = new ServerFacade();
        PreloginREPL prelogin = new PreloginREPL(server);
        prelogin.run();
        out.println("Exited");

    }
}


