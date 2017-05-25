package uy.com.fusion.library.rest.utils;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public abstract class TypeReference<T>
    implements Comparable<TypeReference<T>> {

    final Type _type;

    protected TypeReference() {
        Type superClass = this.getClass().getGenericSuperclass();

        if (superClass instanceof Class<?>) { // sanity check, should never happen
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        }
        /*
         * 22-Dec-2008, tatu: Not sure if this case is safe -- I suspect it is possible to make it fail? But let's deal
         * with specifc case when we know an actual use case, and thereby suitable work arounds for valid case(s) and/or
         * error to throw on invalid one(s).
         */
        this._type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return this._type;
    }

    @SuppressWarnings("unchecked")
    public Class<T> getClazz() {
        return (Class<T>) this.resolveClass(this._type);
    }

    /**
     * The only reason we define this method (and require implementation
     * of <code>Comparable</code>) is to prevent constructing a
     * reference without type information.
     */
    @Override
    public int compareTo(TypeReference<T> o) {
        // just need an implementation, not a good one... hence:
        return 0;
    }

    private Class<?> resolveClass(Type type) {
        // simple class?
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        // But if not, need to start resolving.
        else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof GenericArrayType) {

            Type innerType = ((GenericArrayType) type).getGenericComponentType();
            Class<?> innerClass = this.resolveClass(innerType);
            Object emptyInstance = Array.newInstance(innerClass, 0);
            return emptyInstance.getClass();

        } else if (type instanceof TypeVariable<?>) {

            return Object.class;
        } else if (type instanceof WildcardType) {

            WildcardType wildcardType = (WildcardType) type;
            return this.resolveClass(wildcardType.getUpperBounds()[0]);
        } else {
            // sanity check
            throw new IllegalArgumentException("Unrecognized Type: " + type.toString());
        }
    }

}
