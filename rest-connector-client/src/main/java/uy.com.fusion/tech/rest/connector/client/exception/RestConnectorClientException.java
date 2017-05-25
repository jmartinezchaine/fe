package uy.com.fusion.tech.rest.connector.client.exception;

public class RestConnectorClientException
    extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RestConnectorClientException() {
        super();
    }

    public RestConnectorClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RestConnectorClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestConnectorClientException(String message) {
        super(message);
    }

    public RestConnectorClientException(Throwable cause) {
        super(cause);
    }
}
