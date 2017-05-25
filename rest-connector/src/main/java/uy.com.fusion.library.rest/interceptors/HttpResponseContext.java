package uy.com.fusion.library.rest.interceptors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import uy.com.fusion.library.rest.HttpHeaders;
import uy.com.fusion.library.rest.HttpStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpResponseContext.class);

    private HttpStatus status;
    private HttpHeaders headers;
    private InputStream bodyStream;
    private byte[] body;
    private Map<String, Object> context;
    private boolean streaming;

    public HttpResponseContext(HttpStatus status, HttpHeaders headers, InputStream bodyStream) {
        this.status = status;
        this.headers = headers;
        this.bodyStream = bodyStream;
        this.context = new HashMap<String, Object>();
    }

    public HttpResponseContext(HttpStatus status, HttpHeaders headers, InputStream bodyStream, boolean streaming) {
        this.status = status;
        this.headers = headers;
        this.bodyStream = bodyStream;
        this.context = new HashMap<String, Object>();
        this.streaming = streaming;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public boolean isSuccessfulStreaming() {
        return this.streaming && isTransferEncodingChunked()
            && HttpStatus.Series.SUCCESSFUL == this.status.getSerie();
    }

    public boolean isTransferEncodingChunked() {
        return "chunked".equals(this.headers.getTransferEncoding());
    }

    private byte[] readAllBody() {
        if (this.body == null) {
            try {
                this.body = IOUtils.toByteArray(this.bodyStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    this.bodyStream.close();
                } catch (IOException e) {
                    LOGGER.error("Error closing streaming", e);
                }
            }
        }
        return this.body;
    }

    public InputStream getBodyInputStream() {
        return this.bodyStream;
    }

    public InputStream readAndGetBodyInputStream() {
        return new ByteArrayInputStream(this.readAllBody());
    }

    public void setBodyStream(InputStream bodyStream) {
        this.bodyStream = bodyStream;
        this.body = null;
    }

    public Map<String, Object> getAllContext() {
        return Collections.unmodifiableMap(this.context);
    }

    public void setAllContext(Map<String, Object> context) {
        this.context.putAll(context);
    }

    public Object getContext(String name) {
        return this.context.get(name);
    }

    public void setContext(String name, Object context) {
        this.context.put(name, context);
    }

    @Override
    public HttpResponseContext clone() {
        try {
            byte[] _body = new byte[0];
            _body = IOUtils.toByteArray(this.readAndGetBodyInputStream());
            this.bodyStream.close();
            HttpResponseContext cloned = new HttpResponseContext(this.status, this.headers.clone(),
                new ByteArrayInputStream(_body), this.streaming);
            this.bodyStream = new ByteArrayInputStream(_body);
            cloned.context.putAll(this.context);
            return cloned;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
