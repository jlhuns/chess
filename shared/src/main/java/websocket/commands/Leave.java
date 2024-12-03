package websocket.commands;

public class Leave extends UserGameCommand{
    int gameID;
    public Leave(CommandType commandType, String authToken, Integer gameID) {
        super(CommandType.LEAVE, authToken, gameID);
        this.gameID = gameID;
    }
}
