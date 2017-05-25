package uy.com.fusion.library.rest.connector.exceptions;

// FIXME DD - Borrar esta clase cuando ATP saque la nueva version de su API
@Deprecated
public class ServiceError {

    public static final String DEFAULT_CODE = "4000";

    private String code = DEFAULT_CODE;
    private String description;

    public ServiceError() {
    }

    public ServiceError(String description) {
        this.setCode(DEFAULT_CODE);
        this.setDescription(description);
    }

    public ServiceError(String code, String description) {
        this.setCode(code);
        this.setDescription(description);
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
