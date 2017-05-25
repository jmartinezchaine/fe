package uy.com.fusion.library.rest.serializers;

import java.io.IOException;
import java.io.InputStream;

import uy.com.fusion.library.rest.HttpHeaders;
import uy.com.fusion.library.rest.HttpMethod;
import uy.com.fusion.library.rest.builder.PerRequestConfig;
import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.utils.TypeReference;

public interface Serializer {

    /**
     * Returns true iff this serializer knows how to serialize this body object
     */
    boolean canSerialize(HttpMethod method, String path, HttpHeaders headers, PerRequestConfig perReqConf, Object body);

    /**
     * Takes body and serialize it to byte[]. It can modify everything else.
     *
     * Returns a HttpRequestContext representing the request to send.
     */
    HttpRequestContext serialize(HttpMethod method, String endpoint, String path, HttpHeaders headers, PerRequestConfig perReqConf, Object body) throws IOException;

    /**
     * Returns true iff this serializer knows how to deserialize a InputStream into 'type'.
     * This serializers may want to inspect http headers in order to know what is coming in the InputStream.
     */
    <T> boolean canDeserialize(TypeReference<T> type, HttpHeaders responseHeaders);

    /**
     * Deserializes 'bodyStream' into 'type'.
     */
    <T> T deserialize(TypeReference<T> type, HttpHeaders headers, InputStream bodyStream) throws IOException;
}
