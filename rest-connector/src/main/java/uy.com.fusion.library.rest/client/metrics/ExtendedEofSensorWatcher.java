package uy.com.fusion.library.rest.client.metrics;

import org.apache.http.conn.EofSensorInputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * This is copied from {@link org.apache.http.conn.EofSensorWatcher}, but extended for receiving the total of bytes read
 * before closing or finishing the stream, and the IOException parameter in {@link #streamAbort(InputStream, IOException)}.
 *
 * @author adelio
 * @since 22/07/15
 */
public interface ExtendedEofSensorWatcher {

    /**
     * Indicates that EOF is detected.
     *
     * @param wrapped   the underlying stream which has reached EOF
     *
     * @return  {@code true} if {@code wrapped} should be closed,
     *          {@code false} if it should be left alone
     *
     * @throws IOException
     *         in case of an IO problem, for example if the watcher itself
     *         closes the underlying stream. The caller will leave the
     *         wrapped stream alone, as if {@code false} was returned.
     */
    boolean eofDetected(InputStream wrapped, int bytesRead)
            throws IOException;

    /**
     * Indicates that the {@link EofSensorInputStream stream} is closed.
     * This method will be called only if EOF was <i>not</i> detected
     * before closing. Otherwise, {@link #eofDetected eofDetected} is called.
     *
     * @param wrapped   the underlying stream which has not reached EOF
     *
     * @return  {@code true} if {@code wrapped} should be closed,
     *          {@code false} if it should be left alone
     *
     * @throws IOException
     *         in case of an IO problem, for example if the watcher itself
     *         closes the underlying stream. The caller will leave the
     *         wrapped stream alone, as if {@code false} was returned.
     */
    boolean streamClosed(InputStream wrapped, int bytesRead)
            throws IOException;

    /**
     * Indicates that the {@link EofSensorInputStream stream} is aborted.
     * This method will be called only if EOF was <i>not</i> detected
     * before aborting. Otherwise, {@link #eofDetected eofDetected} is called.
     * <p>
     * This method will also be invoked when an input operation causes an
     * IOException to be thrown to make sure the input stream gets shut down.
     * </p>
     *
     * @param wrapped   the underlying stream which has not reached EOF
     * @param ex        the exception that occurred when accessing the stream
     *
     * @return  {@code true} if {@code wrapped} should be closed,
     *          {@code false} if it should be left alone
     *
     * @throws IOException
     *         in case of an IO problem, for example if the watcher itself
     *         closes the underlying stream. The caller will leave the
     *         wrapped stream alone, as if {@code false} was returned.
     */
    boolean streamAbort(InputStream wrapped, IOException ex)
            throws IOException;

}
