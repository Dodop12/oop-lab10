/**
 * 
 */
package it.unibo.mvc;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This class implements a view that can write on any PrintStream.
 */
public final class PrintStreamView implements DrawNumberView {

    private final PrintStream out;

    /**
     * Builds a new PrintStreamView.
     *
     * @param stream the {@link PrintStream} where to write
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Storing an externally mutable object is done on purpose")
    public PrintStreamView(final PrintStream stream) {
        out = stream;
    }

    /**
     * Builds a {@link PrintStreamView} that writes on file, given a path.
     * 
     * @param path a file path
     * @throws FileNotFoundException        if the file cannot be created
     * @throws UnsupportedEncodingException if the specified charset is not
     *                                      supported
     */
    public PrintStreamView(final String path) throws FileNotFoundException, UnsupportedEncodingException {
        out = new PrintStream(path, "UTF-8");
    }

    @Override
    public void setObserver(final DrawNumberViewObserver observer) {
        /*
         * This UI is output only.
         */
    }

    @Override
    public void start() {
        /*
         * PrintStreams are always ready.
         */
    }

    @Override
    public void numberIncorrect() {
        out.println("You must enter a number");
    }

    @Override
    public void result(final DrawResult res) {
        out.println(res.getDescription());
    }

    @Override
    public void displayError(final String message) {
        out.println("Error: " + message);
    }
}
