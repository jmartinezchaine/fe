package uy.com.fusion.tech.rest.connector.client.exception;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import uy.com.fusion.library.rest.HttpHeaders;
import uy.com.fusion.library.rest.HttpStatus;

public class HttpRestConnectorClientException
    extends RestConnectorClientException {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_CHARSET = "UTF-8";

    private final HttpStatus statusCode;

    private final String statusText;

    private final byte[] responseBody;

    private final String responseCharset;

    private final HttpHeaders responseHeaders;

    public HttpRestConnectorClientException(HttpStatus statusCode, byte[] responseBody) {
        this(statusCode, statusCode.getReasonPhrase(), null, responseBody, null);
    }

    public HttpRestConnectorClientException(HttpStatus statusCode, String statusText, byte[] responseBody) {
        this(statusCode, statusText, null, responseBody, null);
    }

    /**
     * Construct a new instance of {@code HttpRestConnectorClientException} based on an
     * {@link HttpStatus}, status text, and response body content.
     * @param statusCode the status code
     * @param statusText the status text
     * @param responseBody the response body content, may be {@code null}
     * @param responseCharset the response body charset, may be {@code null}
     */
    public HttpRestConnectorClientException(HttpStatus statusCode, String statusText, HttpHeaders responseHeaders, byte[] responseBody,
        Charset responseCharset) {
        super(statusCode.getCode() + " " + statusText + safeBodyString(responseBody, responseCharset));
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody != null ? responseBody : new byte[0];
        this.responseCharset = responseCharset != null ? responseCharset.name() : DEFAULT_CHARSET;
    }

    /**
     * Return the HTTP status code.
     */
    public HttpStatus getStatusCode() {
        return this.statusCode;
    }

    /**
     * Return the HTTP status text.
     */
    public String getStatusText() {
        return this.statusText;
    }

    /**
     * Return the response body as a byte array.
     */
    public byte[] getResponseBodyAsByteArray() {
        return this.responseBody;
    }

    /**
     * Return the HTTP response headers.
     */
    public HttpHeaders getResponseHeaders() {
        return this.responseHeaders;
    }

    /**
     * Return the response body as a string.
     */
    public String getResponseBodyAsString() {
        try {
            return new String(this.responseBody, this.responseCharset);
        } catch (UnsupportedEncodingException ex) {
            // should not occur
            throw new IllegalStateException(ex);
        }
    }

    // Auxiliary Methods
    private static final String safeBodyString(byte[] responseBody, Charset responseCharset) {
        try {
            if (responseBody != null) {
                String charsetName = responseCharset != null ? responseCharset.name() : DEFAULT_CHARSET;
                return " Body: " + new String(responseBody, charsetName);
            }
        } catch (UnsupportedEncodingException e) {
            // Ignore body
        }
        return "";
    }
}
