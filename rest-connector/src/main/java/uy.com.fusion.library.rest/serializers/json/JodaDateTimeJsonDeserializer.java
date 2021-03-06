package uy.com.fusion.library.rest.serializers.json;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JodaDateTimeJsonDeserializer
    extends JsonDeserializer<DateTime> {

    private DateTimeFormatter formatter;

    public JodaDateTimeJsonDeserializer(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING && this.formatter != null) {
            String str = jp.getText().trim();
            if (str.length() == 0) { // [JACKSON-360]
                return null;
            }

            try {
                return this.formatter.parseDateTime(str);
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("Invalid date format '" + str + "'.", jp.getCurrentLocation(), e);
            }
        } else if (t == JsonToken.VALUE_NUMBER_INT && this.formatter == null) {
            long instant = jp.getLongValue();
            return new DateTime(instant);
        } else {
            throw new JsonParseException("Invalid date format." + this.formatter == null ? " A timestamp was expected."
                : " A string format was expected.", jp.getCurrentLocation());
        }
    }
}
