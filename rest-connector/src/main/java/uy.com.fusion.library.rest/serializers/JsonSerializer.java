package uy.com.fusion.library.rest.serializers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import uy.com.fusion.library.rest.HttpHeaders;
import uy.com.fusion.library.rest.HttpMethod;
import uy.com.fusion.library.rest.builder.PerRequestConfig;
import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.utils.TypeReference;

public class JsonSerializer
    implements Serializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSerializer.class);

    private static final String CHARSET_UTF8 = "; charset=utf-8";

    private static final String APPLICATION_JSON = "application/json";

    private ObjectMapper objectMapper;

    private boolean enforceJson;

    public JsonSerializer(ObjectMapper objectMapper) {
        this(objectMapper, false);
    }

    public JsonSerializer(ObjectMapper objectMapper, boolean enforceJson) {
        this.objectMapper = objectMapper;
        this.enforceJson = enforceJson;
    }

    @Override
    public boolean canSerialize(HttpMethod method, String path, HttpHeaders headers, PerRequestConfig perReqConf, Object body) {
        String contentType = headers.getContentTypeMediaType();
        boolean applies = !(body instanceof byte[]) && (APPLICATION_JSON.equals(contentType) || this.enforceJson);
        if (applies) {
            if (APPLICATION_JSON.equals(contentType)) {
                LOGGER.debug("Serialize as Json because Content-Type: application/json");
            } else {
                LOGGER.debug("Serialize as Json because enforceJson == true");
            }
        } else {
            LOGGER.debug("Will not serialize as Json");
        }
        return applies;
    }

    @Override
    public HttpRequestContext serialize(HttpMethod method, String endpoint, String path, HttpHeaders headers,
        PerRequestConfig perReqConf, Object body) throws IOException {
        // encode body as json
        LOGGER.debug("Serializing object as json");
        headers.setContentType(APPLICATION_JSON + CHARSET_UTF8);

        byte[] bodyAsJson = this.objectMapper.writeValueAsBytes(body);

        return new HttpRequestContext(method, endpoint, path, headers, perReqConf, bodyAsJson);
    }

    @Override
    public <T> boolean canDeserialize(TypeReference<T> type, HttpHeaders headers) {
        String contentType = headers.getContentTypeMediaType();
        return APPLICATION_JSON.equals(contentType);
    }

    @Override
    public <T> T deserialize(TypeReference<T> type, HttpHeaders headers, InputStream bodyStream) throws IOException {

        Charset charset = headers.getContentTypeCharset();
        if (charset == null) {
            charset = Charsets.UTF_8;
            LOGGER.debug("No charset received from the server. Using UTF-8 as default.");
        }
        InputStreamReader reader = new InputStreamReader(bodyStream, charset);
        T ret = null;
        if (type.getType() == null) {
            ret = this.objectMapper.readValue(reader, type.getClazz());
        } else {
            ret = this.objectMapper.readValue(reader, this.objectMapper.getTypeFactory().constructType(type.getType()));
        }
        reader.close();
        return ret;
    }
}
