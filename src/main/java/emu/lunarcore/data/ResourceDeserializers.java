package emu.lunarcore.data;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class ResourceDeserializers {

    protected static class LunarCoreDoubleDeserializer implements JsonDeserializer<Double> {
        @Override
        public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return json.getAsDouble();
            } else {
                // FixPoint
                var obj = json.getAsJsonObject();
                return obj.get("Value").getAsDouble();
            }
        }
    }

    protected static class LunarCoreHashDeserializer implements JsonDeserializer<Long> {
        @Override
        public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return json.getAsLong();
            } else {
                // TextID
                var obj = json.getAsJsonObject();
                return obj.get("Hash").getAsLong();
            }
        }
    }
}
