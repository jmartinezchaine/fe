package uy.com.fusion.library.rest.multipart;


public interface Part {

    String getName();

    /**
     * TODO control it when multipart is implemented
     * @return size of this part
     */
    long getSize();
}
