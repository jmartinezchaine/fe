package uy.com.fusion.library.rest.serializers.json;

import java.io.IOException;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JodaLocalDateJsonSerializer
    extends JsonSerializer<LocalDate> {

    private DateTimeFormatter formatter;

    public JodaLocalDateJsonSerializer(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
        JsonProcessingException {

        if (this.formatter != null) {
            jgen.writeString(this.formatter.print(value));
        } else {
            jgen.writeNumber(value.toDate().getTime());
        }
    }
}
