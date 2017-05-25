package uy.com.fusion.library.rest.serializers;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

import uy.com.fusion.library.rest.HttpHeaders;
import uy.com.fusion.library.rest.HttpMethod;
import uy.com.fusion.library.rest.builder.PerRequestConfig;
import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.utils.TypeReference;

public class StringSerializer
    implements Serializer {

    private static final String PLAIN_TEXT = "text/plain";
    private static final String CHARSET_UTF8 = "; charset=utf-8";

    @Override
    public boolean canSerialize(HttpMethod method, String path, HttpHeaders headers, PerRequestConfig perReqConf, Object body) {
        return body instanceof String;
    }

    @Override
    public HttpRequestContext serialize(HttpMethod method, String endpoint, String path, HttpHeaders headers,
        PerRequestConfig perReqConf, Object body) throws IOException {

        if (headers.getContentType() == null) {
            headers.setContentType(PLAIN_TEXT + CHARSET_UTF8);
        } else if (headers.getContentTypeCharset() == null) {
            headers.setContentType(headers.getContentTypeMediaType() + CHARSET_UTF8);
        }

        byte[] byteBody = body.toString().getBytes(headers.getContentTypeCharset());
        return new HttpRequestContext(method, endpoint, path, headers, perReqConf, byteBody);
    }

    @Override
    public <T> boolean canDeserialize(TypeReference<T> type, HttpHeaders headers) {
        return PLAIN_TEXT.equals(headers.getContentTypeMediaType()) || type.getClazz().equals(String.class);
    }

    // TODO: Diferentes encodings
    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(TypeReference<T> type, HttpHeaders headers, InputStream bodyStream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(bodyStream, writer);
        return (T) writer.toString();
    }
}
