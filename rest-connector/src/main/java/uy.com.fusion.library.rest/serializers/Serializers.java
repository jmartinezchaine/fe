package uy.com.fusion.library.rest.serializers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import uy.com.fusion.library.rest.HttpHeaders;
import uy.com.fusion.library.rest.HttpMethod;
import uy.com.fusion.library.rest.builder.PerRequestConfig;
import uy.com.fusion.library.rest.config.RestConnectorConfig;
import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.serializers.json.ObjectMapperFactory;
import uy.com.fusion.library.rest.utils.DependencyUtils;
import uy.com.fusion.library.rest.utils.TypeReference;

public class Serializers {

    private Serializers() {
    }

    private List<Serializer> serializers = new ArrayList<Serializer>();

    public HttpRequestContext serialize(HttpMethod method, String endpoint, String path, HttpHeaders headers, PerRequestConfig perReqConf, Object body)
                    throws IOException {
        if (!method.acceptsBody() || body == null || body instanceof byte[]) {
            byte[] serializedBody = method.acceptsBody() ? (byte[]) body : null;
            HttpRequestContext req = new HttpRequestContext(method, endpoint, path, headers, perReqConf, serializedBody);
            return req;
        }

        for (Serializer s : this.serializers) {
            if (s.canSerialize(method, path, headers, perReqConf, body)) {
                HttpRequestContext req = s.serialize(method, endpoint, path, headers, perReqConf, body);
                return req;
            }
        }

        throw new RuntimeException(
                        String.format("No serializer applies for this request [method: %s; path: %s; headers: %s, body: %s]", method, path, headers, body));
    }

    public <T> T deserialize(TypeReference<T> typeRef, HttpHeaders headers, InputStream bodyStream) throws IOException {
        if (bodyStream == null) {
            return null;
        }

        for (Serializer s : this.serializers) {
            if (s.canDeserialize(typeRef, headers)) {
                T ret = s.deserialize(typeRef, headers, bodyStream);
                return ret;
            }
        }

        byte[] bodyArray = IOUtils.toByteArray(bodyStream);

        if (bodyArray == null || bodyArray.length == 0) {
            return null;
        }

        throw new RuntimeException(String.format("No serializer applies for this response [class: %s; content-type: %s]", typeRef.getClazz().getName(),
                        headers.getContentType()));
    }

    public void add(Serializer serializer) {
        this.serializers.add(serializer);
    }


    // //////////////////////////////////////////////
    // STATIC FACTORY METHODS
    // //////////////////////////////////////////////

    public static Serializers create(Serializer... serializers) {
        Serializers ss = new Serializers();
        for (Serializer s : serializers) {
            ss.add(s);
        }
        return ss;
    }

    /**
     * Serializers: {String}
     */
    public static Serializers createString() {
        return Serializers.create(new StringSerializer());
    }

    /**
     * Serializers: {Json}
     */
    public static Serializers createJson(RestConnectorConfig config) {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper(config.getJsonConfig());
        return Serializers.create(new JsonSerializer(objectMapper));
    }

    /**
     * Serializers: {String, Json, Hessian}
     */
    public static Serializers createfusion(RestConnectorConfig config) {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper(config.getJsonConfig());

        return internalCreateNonJodafusion(objectMapper, config);
    }

    public static Serializers createNonJodafusion(RestConnectorConfig config) {
        ObjectMapper objectMapper = ObjectMapperFactory.getNonJodaObjectMapper(config.getJsonConfig());

        return internalCreateNonJodafusion(objectMapper, config);
    }

    private static Serializers internalCreateNonJodafusion(ObjectMapper objectMapper, RestConnectorConfig config) {
        // Serializers
        Serializers serializers = create(new StringSerializer(), new JsonSerializer(objectMapper), new MultiPartSerializer());

        boolean hessianAvailable = DependencyUtils.isClassPresent("com.caucho.hessian.io.Hessian2Output");

        return serializers;
    }
}
