package uy.com.fusion.library.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;

import uy.com.fusion.library.rest.serializers.Serializers;
import uy.com.fusion.library.rest.utils.TypeReference;

public class HttpResponse {

    private final Serializers serializers;

    protected HttpStatus status;
    protected HttpHeaders headers;
    protected InputStream bodyStream;
    protected byte[] body;

    private AtomicBoolean alreadyRead = new AtomicBoolean(false);

    public HttpResponse(Serializers serializers, HttpStatus status, HttpHeaders headers, InputStream bodyStream) {
        this.serializers = serializers;
        this.status = status;
        this.headers = headers;
        this.bodyStream = bodyStream;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }

    public String getBody() {
        this.readBody();
        if (this.body == null) {
            return null;
        }

        Charset contentTypeCharset = this.headers.getContentTypeCharset();
        return new String(this.body, contentTypeCharset != null ? contentTypeCharset : Charsets.UTF_8);
    }

    public InputStream getBodyAsInputStream() {
        this.readBody();
        if (this.body == null) {
            return null;
        }

        return new ByteArrayInputStream(this.body);
    }

    public byte[] getBodyAsByteArray() {
        this.readBody();

        if (this.body == null) {
            return null;
        }

        return Arrays.copyOf(this.body, this.body.length);
    }

    public <T> T getBodyAs(final Class<T> clazz) throws IOException {
        this.readBody();
        T body = this.getBodyAs(new TypeReference<T>() {
            @Override
            public Type getType() {
                return clazz;
            }
        });
        return body;
    }

    public <T> T getBodyAs(TypeReference<T> typeRef) throws IOException {
        this.readBody();
        if (this.body == null) {
            return null;
        }
        return this.serializers.deserialize(typeRef, this.headers, new ByteArrayInputStream(this.body));
    }

    private void readBody() {
        if (!this.alreadyRead.getAndSet(true)) {
            if (this.bodyStream != null) {
                try {
                    this.body = IOUtils.toByteArray(this.bodyStream);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to read all the body", e);
                } finally {
                    try {
                        this.bodyStream.close();
                    } catch (IOException e) {
                        // Do Nothing
                    }
                }
            } else {
                this.body = null;
            }
        }
    }

}
