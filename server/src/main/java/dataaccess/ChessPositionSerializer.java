package dataaccess;

import chess.ChessPosition;
import com.google.gson.*;

import java.lang.reflect.Type;

class ChessPositionSerializer implements JsonSerializer<ChessPosition> {
    @Override
    public JsonElement serialize(ChessPosition src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("row", src.getRow());
        jsonObject.addProperty("column", src.getColumn());
        return jsonObject;
    }
}

