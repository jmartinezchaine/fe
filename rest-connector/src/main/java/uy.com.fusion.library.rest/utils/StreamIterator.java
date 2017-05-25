package uy.com.fusion.library.rest.utils;

import java.io.Closeable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class StreamIterator<T>
    implements Iterator<T>, Closeable {

    protected T lastValue;

    protected abstract T readNext();

    @Override
    public boolean hasNext() {
        if (this.lastValue == null) {
            this.lastValue = this.readNext();
        }
        return this.lastValue != null;
    }

    @Override
    public T next() {
        if (this.hasNext()) {
            T ret = this.lastValue;
            this.lastValue = null;
            return ret;
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
