package uy.com.fusion.library.rest.client.metrics;

import java.io.IOException;
import java.io.InputStream;

public class ResponseEndTimestampWatcher
        implements ExtendedEofSensorWatcher {

    private MetricRegistry.MetricRegistryBuilder builder;

    public ResponseEndTimestampWatcher(MetricRegistry.MetricRegistryBuilder builder) {
        this.builder = builder;
    }

    /**
     * This is sensed from inner stream. Thus, we must return false in order not to generate a fake #close call on the
     * inner stream (which probably already reacted to the eof).
     * Correction: it looks like it should return true.
     */
    @Override
    public boolean eofDetected(InputStream wrapped, int bytesRead) throws IOException {
        finishMetricBuilder(bytesRead);
        return true;
    }

    /**
     * We need to propagate the close to the inner stream, as #close is not a state sensed from the inner stream, but
     * an action triggered from the outer execution thread.
     */
    @Override
    public boolean streamClosed(InputStream wrapped, int bytesRead) throws IOException {
        finishMetricBuilder(bytesRead);
        return true;
    }

    /**
     * This is sensed from inner stream. Thus, we must return false in order not to generate a fake #close call on the
     * inner stream (which probably already reacted to the abort).
     */
    @Override
    public boolean streamAbort(InputStream wrapped, IOException ex) throws IOException {
        builder.withResponseException(ex);
        finishMetricBuilder(0);
        return true;
    }

    private void finishMetricBuilder(int bytesRead) {
        builder.buildAndDispatch(bytesRead, System.nanoTime());
    }
}
