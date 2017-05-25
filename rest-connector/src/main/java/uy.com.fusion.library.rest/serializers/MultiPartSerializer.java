package uy.com.fusion.library.rest.serializers;

import java.io.IOException;
import java.io.InputStream;

import uy.com.fusion.library.rest.HttpBodyParts;
import uy.com.fusion.library.rest.HttpHeaders;
import uy.com.fusion.library.rest.HttpMethod;
import uy.com.fusion.library.rest.builder.PerRequestConfig;
import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.utils.TypeReference;

/**
 * @author jformoso
 */
public class MultiPartSerializer
    implements Serializer {

    @Override
    public boolean canSerialize(HttpMethod method, String path, HttpHeaders headers, PerRequestConfig perReqConf, Object body) {
        return body instanceof HttpBodyParts;
    }

    @Override
    public HttpRequestContext serialize(HttpMethod method, String endpoint, String path, HttpHeaders headers, PerRequestConfig perReqConf, Object body) throws IOException {
        return new HttpRequestContext(method, endpoint, path, headers, perReqConf, null, (HttpBodyParts) body);
    }

    @Override
    public <T> boolean canDeserialize(TypeReference<T> type, HttpHeaders headers) {
        return false;
    }

    @Override
    public <T> T deserialize(TypeReference<T> type, HttpHeaders headers, InputStream bodyStream) throws IOException {
        throw new RuntimeException("multipart deserialization not supported");
    }

}
