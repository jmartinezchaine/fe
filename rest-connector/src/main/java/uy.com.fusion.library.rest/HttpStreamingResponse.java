package uy.com.fusion.library.rest;

import java.io.IOException;
import java.io.InputStream;

import uy.com.fusion.library.rest.serializers.Serializers;
import uy.com.fusion.library.rest.serializers.StreamSerializers;
import uy.com.fusion.library.rest.utils.StreamIterator;
import uy.com.fusion.library.rest.utils.TypeReference;

public class HttpStreamingResponse
    extends HttpResponse {

    private final StreamSerializers streamSerializers;

    public HttpStreamingResponse(Serializers serializers, StreamSerializers streamSerializers, HttpStatus status,
        HttpHeaders headers, InputStream bodyStream) {
        super(serializers, status, headers, bodyStream);
        this.streamSerializers = streamSerializers;
    }

    public <T> StreamIterator<T> getBodyAsStreamOf(final Class<T> clazz) throws IOException {
        return this.getBodyAsStreamOf(new TypeReference<T>() {
            @Override
            public Class<T> getClazz() {
                return clazz;
            }
        });
    }

    public <T> StreamIterator<T> getBodyAsStreamOf(final TypeReference<T> typeRef) throws IOException {
        try {
            return this.streamSerializers.deserialize(typeRef, this.headers, this.bodyStream);
        } catch (Exception e) {
            this.bodyStream.close();
            throw new IOException(e);
        }
    }
}
