package uy.com.fusion.library.rest;

public enum HttpMethod {

    HEAD, GET, PUT, POST, DELETE, PATCH, OPTIONS, TRACE;

    public boolean acceptsBody() {
        switch (this) {
        case TRACE:
            return false;
        default:
            return true;
        }
    }
}
