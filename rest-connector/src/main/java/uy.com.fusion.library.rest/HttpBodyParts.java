package uy.com.fusion.library.rest;

import java.util.ArrayList;
import java.util.List;

import uy.com.fusion.library.rest.multipart.Part;

public class HttpBodyParts {

    private List<Part> parts = new ArrayList<Part>();

    public List<Part> getParts() {
        return this.parts;
    }

    @Override
    public HttpBodyParts clone() {
        HttpBodyParts ret = new HttpBodyParts();
        for (int i = 0; i < this.parts.size(); i++) {
            ret.getParts().add(this.parts.get(i));
        }
        return ret;
    }
}
