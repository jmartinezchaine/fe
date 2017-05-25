package uy.com.fusion.library.rest.serializers;

import java.io.IOException;
import java.io.InputStream;

import uy.com.fusion.library.rest.HttpHeaders;
import uy.com.fusion.library.rest.utils.StreamIterator;
import uy.com.fusion.library.rest.utils.TypeReference;

public interface StreamDeserializer {

    /**
     * Returns true iff this serializer knows how to deserialize a InputStream into 'type'.
     * This serializers may want to inspect http headers in order to know what is coming in the InputStream.
     */
    <T> boolean canDeserialize(TypeReference<T> type, HttpHeaders responseHeaders);

    /**
     * Deserializes 'chunkedBodyStream' into an iterator of 'type'.
     */
    <T> StreamIterator<T> deserialize(TypeReference<T> type, HttpHeaders headers, InputStream bodyStream) throws IOException;

}
