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
import com.fasterxml.jackson.core.JsonParseException;

@NotThreadSafe
public class StringStreamSerializer
    implements StreamDeserializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonStreamSerializer.class);

    private static final String PLAIN_TEXT = "text/plain";

    @Override
    public <T> boolean canDeserialize(TypeReference<T> type, HttpHeaders headers) {
        String contentType = headers.getContentTypeMediaType();
        return PLAIN_TEXT.equals(contentType) || type.getClazz().equals(String.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> StreamIterator<T> deserialize(final TypeReference<T> type, HttpHeaders headers,
        final InputStream chunkedBodyStream) throws IOException {

        Charset charset = headers.getContentTypeCharset();
        if (charset == null) {
            charset = Charsets.UTF_8;
            LOGGER.debug("No charset received from the server. Using UTF-8 as default.");
        }

        return (StreamIterator<T>) new StringStreamIterator(chunkedBodyStream);
    }
}


@NotThreadSafe
class StringStreamIterator
    extends StreamIterator<String> {

    private final byte[] BUFFER = new byte[8 * 1024 * 1024];

    private InputStream chunkedBodyStream;
    private boolean closed = false;

    public StringStreamIterator(InputStream chunkedBodyStream) throws JsonParseException, IOException {
        this.chunkedBodyStream = chunkedBodyStream;
    }

    @Override
    protected String readNext() {
        try {
            int length = this.chunkedBodyStream.read(this.BUFFER);
            this.closed = true;
            if (length != -1) {
                return new String(this.BUFFER, 0, length);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.chunkedBodyStream.close();
            this.closed = true;
        }
    }
}
