package uy.com.fusion.tech.rest.connector.client;

public class ResponseContainer<T> {
    private T data;

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
