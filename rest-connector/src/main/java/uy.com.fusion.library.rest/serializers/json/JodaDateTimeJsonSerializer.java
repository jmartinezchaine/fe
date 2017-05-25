package uy.com.fusion.library.rest.serializers.json;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JodaDateTimeJsonSerializer
    extends JsonSerializer<DateTime> {

    private DateTimeFormatter formatter;

    public JodaDateTimeJsonSerializer(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
        JsonProcessingException {

        if (this.formatter != null) {
            jgen.writeString(this.formatter.print(value));
        } else {
            jgen.writeNumber(value.getMillis());
        }
    }
}
