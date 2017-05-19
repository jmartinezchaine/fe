package uy.com.fusion.fe.web.api.document;

import java.io.Serializable;

/**
 * Created by juanmartinez on 18/5/17.
 */
public class DocumentEx
                implements Serializable {

    private String id;
    private String name;
    private String description;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "DocumentEx{" +
                        "id='" + id + '\'' +
                        ", descripcion='" + description + '\'' +
                        ", type='" + type + '\'' +
                        '}';
    }
}
