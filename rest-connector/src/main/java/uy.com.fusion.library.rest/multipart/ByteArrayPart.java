package uy.com.fusion.library.rest.multipart;


public class ByteArrayPart
    implements Part {
    private String name;
    private String fileName;
    private byte[] data;
    private String mimeType;
    private String charSet;

    public ByteArrayPart(String name, String fileName, byte[] data) {
        this(name, fileName, data, null, null);
    }

    public ByteArrayPart(String name, String fileName, byte[] data, String mimeType, String charSet) {
        this.name = name;
        this.fileName = fileName;
        this.data = data;
        this.mimeType = mimeType;
        this.charSet = charSet;
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
        return data != null ? data.length : 0;
    }

    public String getFileName() {
        return this.fileName;
    }

    public byte[] getData() {
        return this.data;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String getCharSet() {
        return this.charSet;
    }
}
