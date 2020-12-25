package arbyte.helper;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

// credit: https://github.com/gkopff/gson-javatime-serialisers/blob/master/src/main/java/com/fatboyindustrial/gsonjavatime/ZonedDateTimeConverter.java
public class ZonedDateTimeConverter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    @Override
    public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(FORMATTER.format(src));
    }

    @Override
    public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        return FORMATTER.parse(json.getAsString(), ZonedDateTime::from);
    }
}
