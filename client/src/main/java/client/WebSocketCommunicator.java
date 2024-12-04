package client;

import chess.ChessBoard;
import chess.ChessGame;

import com.google.gson.*;
import ui.GamePlayREPL;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.Error;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


import static java.lang.System.out;
import static ui.EscapeSequences.ERASE_LINE;

public class WebSocketCommunicator extends Endpoint {

    public Session session;
    GamePlayREPL gamePlayREPL;

    public WebSocketCommunicator(String serverDomain) throws Exception {
        try{
            URI uri = new URI("ws://" + serverDomain + "/connect");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    handleMessage(message);
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception();
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        out.println("Connected to server");
    }

    private void handleMessage(String message) {
        out.println(message);
        if (message.contains("\"serverMessageType\":\"NOTIFICATION\"")) {
            Notification notif = new Gson().fromJson(message, Notification.class);
            printNotification(notif.getMessage());
        }
        else if (message.contains("\"serverMessageType\":\"ERROR\"")) {
            Error error = new Gson().fromJson(message, Error.class);
            printNotification(error.getMessage());
        }
        else if (message.contains("\"serverMessageType\":\"LOAD_GAME\"")) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ChessBoard.class, new ChessBoardDeserializer())
                    .create();
            LoadGame loadGame = gson.fromJson(message, LoadGame.class);
            printLoadedGame(loadGame.getGame());
        }
    }

    private void printNotification(String message) {
        out.print(ERASE_LINE + '\r');
        out.printf("\n%s\n[IN-GAME] >>> ", message);
    }

    private void printLoadedGame(ChessGame game) {
        out.print(ERASE_LINE + "\r\n");
        GamePlayREPL.boardPrint.updateGame(game);
        GamePlayREPL.boardPrint.printBoard(GamePlayREPL.color);
        out.print("[IN-GAME] >>> ");
    }

    public void sendMessage(String message) {
        this.session.getAsyncRemote().sendText(message);
    }
}