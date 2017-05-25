package uy.com.fusion.library.rest.multipart;

public class StringPart
    implements Part {
    private final String name;
    private final String value;
    private final String charset;

    public StringPart(String name, String value) {
        this(name, value, "UTF-8");
    }

    public StringPart(String name, String value, String charset) {
        this.name = name;
        this.value = value;
        this.charset = charset;
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
    public String getName() {
        return this.name;
    }

    @Override
    public long getSize() {
        long size = value != null ? value.length() : 0;
        size += name != null ? name.length() : 0;
        return size;
    }

    public String getValue() {
        return this.value;
    }

    public String getCharset() {
        return this.charset;
    }
}
