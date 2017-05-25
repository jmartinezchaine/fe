package uy.com.fusion.library.rest.serializers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uy.com.fusion.library.rest.HttpHeaders;
import uy.com.fusion.library.rest.utils.StreamIterator;
import uy.com.fusion.library.rest.utils.TypeReference;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

@NotThreadSafe
public class JsonStreamSerializer
    implements StreamDeserializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonStreamSerializer.class);

    private static final String APPLICATION_JSON = "application/json";

    private JsonFactory factory;

    public JsonStreamSerializer(ObjectMapper objectMapper) {
        this.factory = objectMapper.getFactory();
    }

    @Override
    public <T> boolean canDeserialize(TypeReference<T> type, HttpHeaders headers) {
        String contentType = headers.getContentTypeMediaType();
        return APPLICATION_JSON.equals(contentType);
    }

    @Override
    public <T> StreamIterator<T> deserialize(final TypeReference<T> type, HttpHeaders headers,
        final InputStream chunkedBodyStream) throws IOException {

        Charset charset = headers.getContentTypeCharset();
        if (charset == null) {
            charset = Charsets.UTF_8;
            LOGGER.debug("No charset received from the server. Using UTF-8 as default.");
        }

        return new JsonStreamIterator<T>(type, chunkedBodyStream, this.factory);
    }
}


@NotThreadSafe
class JsonStreamIterator<T>
    extends StreamIterator<T> {

    private TypeReference<T> type;
    private JsonParser parser;

    public JsonStreamIterator(TypeReference<T> type, InputStream chunkedBodyStream, JsonFactory factory)
        throws JsonParseException, IOException {
        this.type = type;
        this.parser = factory.createParser(chunkedBodyStream);
        JsonToken firstToken = this.parser.nextToken();
        assert firstToken == JsonToken.START_ARRAY;
    }

    @Override
    protected T readNext() {
        try {
            JsonToken nextToken = this.parser.nextToken();

            if (nextToken == JsonToken.START_OBJECT) {
                return this.parser.readValueAs(this.type.getClazz());
            } else if (nextToken == null || nextToken == JsonToken.END_ARRAY) {
                this.parser.close();
                return null;
            } else {
                this.parser.close();
                throw new IllegalStateException("Inconsistent json, expected JsonToken.START_OBJECT got " + nextToken);
            }
        } catch (Exception ex) {
            try {
                this.parser.close();
            } catch (IOException e) {

            }
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() throws IOException {
        if (!this.parser.isClosed()) {
            this.parser.close();
        }
    }
}
