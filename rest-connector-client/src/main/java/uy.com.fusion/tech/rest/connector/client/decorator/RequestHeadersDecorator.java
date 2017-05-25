package uy.com.fusion.tech.rest.connector.client.decorator;


import uy.com.fusion.library.rest.HttpHeaders;

public interface RequestHeadersDecorator {
    void decorate(HttpHeaders headers);
}
