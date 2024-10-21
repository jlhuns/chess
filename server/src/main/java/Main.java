import chess.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class Server {
    private HttpServer server;

    public void run(int port) {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", httpExchange -> {
                String response = "♕ Welcome to 240 Chess Server!";
                httpExchange.sendResponseHeaders(200, response.getBytes().length);
                try (var os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            });
            server.start();
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
    }
}