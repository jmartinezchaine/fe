package uy.com.fusion.library.rest.client.metrics;

import org.apache.http.util.Args;

import java.io.IOException;
import java.io.InputStream;


public class ExtendedEofSensorInputStream extends InputStream {

    private int bytesRead;

    /**
     * The wrapped input stream, while accessible.
     * The value changes to {@code null} when the wrapped stream
     * becomes inaccessible.
     */
    protected InputStream wrappedStream;

    /**
     * Indicates whether this stream itself is closed.
     * If it isn't, but {@link #wrappedStream wrappedStream}
     * is {@code null}, we're running in EOF mode.
     * All read operations will indicate EOF without accessing
     * the underlying stream. After closing this stream, read
     * operations will trigger an {@link IOException IOException}.
     *
     * @see #isReadAllowed isReadAllowed
     */
    private boolean selfClosed;

    /** The watcher to be notified, if any. */
    private final ExtendedEofSensorWatcher eofWatcher;

    /**
     * Creates a new EOF sensor.
     * If no watcher is passed, the underlying stream will simply be
     * closed when EOF is detected or {@link #close close} is called.
     * Otherwise, the watcher decides whether the underlying stream
     * should be closed before detaching from it.
     *
     * @param in        the wrapped stream
     * @param watcher   the watcher for events, or {@code null} for
     *                  auto-close behavior without notification
     */
    public ExtendedEofSensorInputStream(final InputStream in, final ExtendedEofSensorWatcher watcher) {
        Args.notNull(in, "Wrapped stream");
        wrappedStream = in;
        selfClosed = false;
        eofWatcher = watcher;
        bytesRead = 0;
    }

    boolean isSelfClosed() {
        return selfClosed;
    }

    InputStream getWrappedStream() {
        return wrappedStream;
    }

    /**
     * Checks whether the underlying stream can be read from.
     *
     * @return  {@code true} if the underlying stream is accessible,
     *          {@code false} if this stream is in EOF mode and
     *          detached from the underlying stream
     *
     * @throws IOException      if this stream is already closed
     */
    protected boolean isReadAllowed() throws IOException {
        if (selfClosed) {
            throw new IOException("Attempted read on closed stream.");
        }
        return (wrappedStream != null);
    }

    @Override
    public int read() throws IOException {
        int l = -1;

        if (isReadAllowed()) {
            try {
                l = wrappedStream.read();
                if (l > 0) { // it could be -1
                    bytesRead++;
                }
                checkEOF(l);
            } catch (final IOException ex) {
                checkAbort(ex);
                throw ex;
            }
        }

        return l;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        int l = -1;

        if (isReadAllowed()) {
            try {
                l = wrappedStream.read(b,  off,  len);
                if (l > 0) { // it could be -1
                    bytesRead += l;
                }
                checkEOF(l);
            } catch (final IOException ex) {
                checkAbort(ex);
                throw ex;
            }
        }

        return l;
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int available() throws IOException {
        int a = 0; // not -1

        if (isReadAllowed()) {
            try {
                a = wrappedStream.available();
                // no checkEOF() here, available() can't trigger EOF
            } catch (final IOException ex) {
                checkAbort(ex);
                throw ex;
            }
        }

        return a;
    }

    @Override
    public void close() throws IOException {
        // tolerate multiple calls to close()
        selfClosed = true;
        checkClose();
    }

    /**
     * Detects EOF and notifies the watcher.
     * This method should only be called while the underlying stream is
     * still accessible. Use {@link #isReadAllowed isReadAllowed} to
     * check that condition.
     * <p>
     * If EOF is detected, the watcher will be notified and this stream
     * is detached from the underlying stream. This prevents multiple
     * notifications from this stream.
     * </p>
     *
     * @param eof       the result of the calling read operation.
     *                  A negative value indicates that EOF is reached.
     *
     * @throws IOException
     *          in case of an IO problem on closing the underlying stream
     */
    protected void checkEOF(final int eof) throws IOException {

        if ((wrappedStream != null) && (eof < 0)) {
            try {
                boolean scws = true; // should close wrapped stream?
                if (eofWatcher != null) {
                    scws = eofWatcher.eofDetected(wrappedStream, bytesRead);
                }
                if (scws) {
                    wrappedStream.close();
                }
            } finally {
                wrappedStream = null;
            }
        }
    }

    /**
     * Detects stream close and notifies the watcher.
     * There's not much to detect since this is called by {@link #close close}.
     * The watcher will only be notified if this stream is closed
     * for the first time and before EOF has been detected.
     * This stream will be detached from the underlying stream to prevent
     * multiple notifications to the watcher.
     *
     * @throws IOException
     *          in case of an IO problem on closing the underlying stream
     */
    protected void checkClose() throws IOException {

        if (wrappedStream != null) {
            try {
                boolean scws = true; // should close wrapped stream?
                if (eofWatcher != null) {
                    scws = eofWatcher.streamClosed(wrappedStream, bytesRead);
                }
                if (scws) {
                    wrappedStream.close();
                }
            } finally {
                wrappedStream = null;
            }
        }
    }

    /**
     * Detects stream abort and notifies the watcher.
     * The watcher will only be notified if this stream is aborted
     * for the first time and before EOF has been detected or the
     * stream has been {@link #close closed} gracefully.
     * This stream will be detached from the underlying stream to prevent
     * multiple notifications to the watcher.
     *
     * @throws IOException
     *          in case of an IO problem on closing the underlying stream
     */
    protected void checkAbort(IOException ex) throws IOException {

        if (wrappedStream != null) {
            try {
                boolean scws = true; // should close wrapped stream?
                if (eofWatcher != null) {
                    scws = eofWatcher.streamAbort(wrappedStream, ex);
                }
                if (scws) {
                    wrappedStream.close();
                }
            } finally {
                wrappedStream = null;
            }
        }
    }

}
