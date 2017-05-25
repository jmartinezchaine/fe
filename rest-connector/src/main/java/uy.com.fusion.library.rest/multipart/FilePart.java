package uy.com.fusion.library.rest.multipart;

import java.io.File;


public class FilePart
    implements Part {
    private String name;
    private File file;
    private String mimeType;
    private String charSet;

    public FilePart(String name, File file) {
        this(name, file, null, null);
    }

    public FilePart(String name, File file, String mimeType, String charSet) {
        this.name = name;
        this.file = file;
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
        long size = file != null ? file.getTotalSpace() : 0;
        size += mimeType != null ? mimeType.length() : 0;
        size += name != null ? name.length() : 0;
        return size;
    }

    public File getFile() {
        return this.file;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String getCharSet() {
        return this.charSet;
    }
}
