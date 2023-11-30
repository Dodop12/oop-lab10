package it.unibo.mvc;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver {

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * @param views
     *              the views to attach
     */
    public DrawNumberApp(final DrawNumberView... views) {
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view : views) {
            view.setObserver(this);
            view.start();
        }

        final Configuration configuration = new ConfigFromFile(views).getConfBuilder().build();
        if (!configuration.isConsistent()) {
            this.model = new DrawNumberImpl(new Configuration.Builder().build());
            displayErrorAll("Invalid configuration (min: " + configuration.getMin() + ", max: " + configuration.getMax()
                    + ", attempts: " + configuration.getAttempts() + "). Default value have been set.", views);
        } else {
            this.model = new DrawNumberImpl(configuration);
        }
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view : views) {
                view.result(result);
            }
        } catch (IllegalArgumentException e) {
            for (final DrawNumberView view : views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    public static void displayErrorAll(final String error, final DrawNumberView... views) {
        for (final var view : views) {
            view.displayError(error);
        }
    }

    /**
     * @param args
     *             ignored
     * @throws FileNotFoundException
     */
    public static void main(final String... args) throws FileNotFoundException {
        new DrawNumberApp(new DrawNumberViewImpl(), new DrawNumberViewImpl(), new PrintStreamView(System.out),
                new PrintStreamView("log.txt"));
    }

}
