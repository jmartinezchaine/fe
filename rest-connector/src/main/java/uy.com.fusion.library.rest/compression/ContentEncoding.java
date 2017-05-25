package uy.com.fusion.library.rest.compression;

public enum ContentEncoding {

    IDENTITY("identity"), DEFLATE("deflate"), GZIP("gzip"), SNAPPY("x-snappy");

    private String headerValue;

    ContentEncoding(String headerValue) {
        this.headerValue = headerValue;
    }

    public String getHeaderValue() {
        return this.headerValue;
    }

    public static ContentEncoding fromHeaderValue(String headerValue) {
        switch(headerValue) {
            case "gzip"    : return ContentEncoding.GZIP;
            case "snappy"  : return ContentEncoding.SNAPPY;
            case "deflate" : return ContentEncoding.DEFLATE;
            case "identity": return ContentEncoding.IDENTITY;
            default        : throw new IllegalArgumentException("Unsupported content encoding: " + headerValue);
        }
    }

}
