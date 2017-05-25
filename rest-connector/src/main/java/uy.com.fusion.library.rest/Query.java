package uy.com.fusion.library.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class Query {


    public static Query build(String queryTemplate, Object... arguments) {
        if (queryTemplate == null || queryTemplate.trim().isEmpty()) {
            throw new IllegalArgumentException("'pathTemplate' can't be null or empty ");
        }

        String[] split = queryTemplate.split("\\?");
        String pathTemplate = split[0];

        String[] pathFolders = pathTemplate.split("/");
        StringBuilder path = new StringBuilder();
        int argumentsIndex = 0;

        for (String folder : pathFolders) {
            if (folder.isEmpty()) {
                continue;
            }

            if (folder.startsWith(":")) {
                path.append("/").append(arguments[argumentsIndex++]);
            } else {
                path.append("/").append(folder);
            }
        }

        Query query = new Query(path.toString());

        if (split.length > 1) {
            String queryStringTemplate = split[1];
            String[] paramAndValues = queryStringTemplate.split("&");

            for (String pv : paramAndValues) {
                String[] pvSplit = pv.split("=");
                String param = pvSplit[0];
                String value = pvSplit[1];
                if (value.startsWith(":")) {
                    Object object = arguments[argumentsIndex++];
                    if (object instanceof Iterable<?>) {
                        Iterable<?> iterable = (Iterable<?>) object;
                        for (Object parameter : iterable) {
                            query.add(param, parameter.toString());
                        }
                    } else if (object instanceof Object[]) {
                        Object[] arr = (Object[]) object;
                        for (Object parameter : arr) {
                            query.add(param, parameter.toString());
                        }
                    } else {
                        query.add(param, object.toString());
                    }
                } else {
                    query.add(param, value);
                }
            }

        }

        return query;
    }

    private final String path;
    private final Set<Parameter> queryStringParams;

    private Query(String path) {
        this.path = path;
        this.queryStringParams = new LinkedHashSet<Query.Parameter>();
    }

    public Query add(String queryStringParam, String value) {
        try {

            value = URLEncoder.encode(value, "UTF-8");
            this.queryStringParams.add(new Parameter(queryStringParam, value));
            return this;

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.path);
        if (!this.queryStringParams.isEmpty()) {

            sb.append("?");

            Iterator<Parameter> it = this.queryStringParams.iterator();
            Parameter e = it.next();
            sb.append(e.getName()).append("=").append(e.getValue());

            while (it.hasNext()) {
                e = it.next();
                sb.append("&").append(e.getName()).append("=").append(e.getValue());
            }
        }
        return sb.toString();
    }

    private class Parameter {
        private String name;
        private String value;

        public Parameter(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.getOuterType().hashCode();
            result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
            result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            Parameter other = (Parameter) obj;
            if (!this.getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (this.name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!this.name.equals(other.name)) {
                return false;
            }
            if (this.value == null) {
                if (other.value != null) {
                    return false;
                }
            } else if (!this.value.equals(other.value)) {
                return false;
            }
            return true;
        }

        private Query getOuterType() {
            return Query.this;
        }
    }

}
