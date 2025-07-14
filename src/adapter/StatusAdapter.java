package adapter;

import com.google.gson.*;
import model.Status;

import java.lang.reflect.Type;

public class StatusAdapter implements JsonSerializer<Status>, JsonDeserializer<Status> {
    @Override
    public JsonElement serialize(Status status, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(status.name());
    }

    @Override
    public Status deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            return Status.valueOf(json.getAsString());
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Некорректный статус: " + json.getAsString());
        }
    }
}