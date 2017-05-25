package uy.com.fusion.tech.rest.connector.client.serializer;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import uy.com.fusion.library.rest.HttpHeaders;
import uy.com.fusion.library.rest.HttpMethod;
import uy.com.fusion.library.rest.builder.PerRequestConfig;
import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.serializers.Serializer;
import uy.com.fusion.library.rest.utils.TypeReference;

public class ByteArrayXmlSerializer
    implements Serializer {

    private static final String APPLICATION_XML = "application/xml";

    @Override
    public HttpRequestContext serialize(HttpMethod method, String endpoint, String path, HttpHeaders headers, PerRequestConfig perReqConf, Object body)
        throws IOException {
        throw new RuntimeException("Serialization not supported.");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(TypeReference<T> type, HttpHeaders headers, InputStream bodyStream) throws IOException {
        return (T) IOUtils.toByteArray(bodyStream);
    }

    @Override
    public boolean canSerialize(HttpMethod method, String path, HttpHeaders headers, PerRequestConfig perReqConf, Object body) {
        return false;
    }

    @Override
    public <T> boolean canDeserialize(TypeReference<T> type, HttpHeaders headers) {
        String contentType = headers.getContentTypeMediaType();
        return APPLICATION_XML.equals(contentType);
    }
}
