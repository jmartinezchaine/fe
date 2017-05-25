package uy.com.fusion.library.rest.serializers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import uy.com.fusion.library.rest.HttpHeaders;
import uy.com.fusion.library.rest.config.RestConnectorConfig;
import uy.com.fusion.library.rest.serializers.json.ObjectMapperFactory;
import uy.com.fusion.library.rest.utils.StreamIterator;
import uy.com.fusion.library.rest.utils.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StreamSerializers {

    private StreamSerializers() {
    }

    private List<StreamDeserializer> serializers = new ArrayList<StreamDeserializer>();

    public <T> StreamIterator<T> deserialize(TypeReference<T> typeRef, HttpHeaders headers, InputStream bodyStream)
        throws IOException {
        if (bodyStream == null) {
            return null;
        }

        for (StreamDeserializer s : this.serializers) {
            if (s.canDeserialize(typeRef, headers)) {
                StreamIterator<T> ret = s.deserialize(typeRef, headers, bodyStream);
                return ret;
            }
        }

        throw new RuntimeException(String.format("No serializer applies for this response [class: %s; content-type: %s]",
            typeRef.getClazz().getName(), headers.getContentType()));
    }

    public void add(StreamDeserializer serializer) {
        this.serializers.add(serializer);
    }


    // //////////////////////////////////////////////
    // STATIC FACTORY METHODS
    // //////////////////////////////////////////////

    public static StreamSerializers create(StreamDeserializer... serializers) {
        StreamSerializers ss = new StreamSerializers();
        for (StreamDeserializer s : serializers) {
            ss.add(s);
        }
        return ss;
    }

    /**
     * Serializers: {String}
     */
    public static StreamSerializers createString() {
        return StreamSerializers.create(new StringStreamSerializer());
    }

    /**
     * Serializers: {Json}
     */
    public static StreamSerializers createJson(RestConnectorConfig config) {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper(config.getJsonConfig());
        return StreamSerializers.create(new JsonStreamSerializer(objectMapper));
    }

    public static StreamSerializers createfusion(RestConnectorConfig config) {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper(config.getJsonConfig());
        return StreamSerializers.create(new StringStreamSerializer(), new JsonStreamSerializer(objectMapper));
    }

    public static StreamSerializers createNonJodafusion(RestConnectorConfig config) {
        ObjectMapper objectMapper = ObjectMapperFactory.getNonJodaObjectMapper(config.getJsonConfig());
        return StreamSerializers.create(new StringStreamSerializer(), new JsonStreamSerializer(objectMapper));
    }
}
