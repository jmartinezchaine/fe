package uy.com.fusion.tech.rest.connector.client;

public interface CustomDataBuilder<REQ extends Object, RES extends Object> {
    Object build(REQ requestBody, RES responseBody);
}
