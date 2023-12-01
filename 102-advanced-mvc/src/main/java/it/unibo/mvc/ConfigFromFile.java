package it.unibo.mvc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * Reads the application settings from the configuration file provided.
 */
public final class ConfigFromFile {

    private static final String FILE_NAME = "config.yml";
    private static final String DEFAULTVALUES_ERROR = "Default values have been set.";
    private static final String MIN = "minimum";
    private static final String MAX = "maximum";
    private static final String ATTEMPTS = "attempts";

    private final Configuration.Builder confBuilder;

    /**
     * @param views the views to attach
     */
    public ConfigFromFile(final DrawNumberView... views) {
        confBuilder = new Configuration.Builder();
        int lineNumber = 1; // Used by the error log

        try {
            // Searches for the specified file in the class path and gets its URL
            final var fileURL = ClassLoader.getSystemResource(FILE_NAME);

            if (Objects.isNull(fileURL)) {
                DrawNumberApp.displayErrorAll("Cannot find the file '" + FILE_NAME + "'. " + DEFAULTVALUES_ERROR,
                        views);
            } else {
                final Path filePath = Path.of(fileURL.toURI());
                final List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

                for (final String line : lines) {
                    // Splits the lines into tokens (words separated by a colon and a space by
                    // default)
                    final var tokenizer = new StringTokenizer(line, ": ");

                    // Each line of the file must have the format 'attribute: value' (colon and
                    // space between the two are needed)
                    if (tokenizer.countTokens() == 2) {
                        // First word of the line; ignoring case
                        final String attribute = tokenizer.nextToken().toLowerCase(Locale.ROOT);
                        // Second word of the line (must be an integer)
                        final int value = Integer.parseInt(tokenizer.nextToken());

                        switch (attribute) {
                            case MIN -> confBuilder.setMin(value);
                            case MAX -> confBuilder.setMax(value);
                            case ATTEMPTS -> confBuilder.setAttempts(value);
                            default -> DrawNumberApp.displayErrorAll(
                                    "Configuration file format error: invalid attribute (line " + lineNumber + ")",
                                    views);
                        }
                    } else {
                        DrawNumberApp
                                .displayErrorAll(
                                        "Configuration file format error: each line must contain exactly two words (line "
                                                + lineNumber + ")",
                                        views);
                    }
                    lineNumber++;
                }
            }
        } catch (final IOException e) {
            DrawNumberApp.displayErrorAll("Cannot read the file '" + FILE_NAME + "'. " + DEFAULTVALUES_ERROR,
                    views);
        } catch (final NumberFormatException e) {
            DrawNumberApp.displayErrorAll(
                    "Invalid configuration file format. " + e.getMessage() + " (line " + lineNumber + "). "
                            + DEFAULTVALUES_ERROR,
                    views);
        } catch (final URISyntaxException e) {
            DrawNumberApp.displayErrorAll(
                    "Cannot read the file path. " + e.getMessage() + ". " + DEFAULTVALUES_ERROR,
                    views);
        }
    }

    /**
     * @return the configuration builder
     */
    public Configuration.Builder getConfBuilder() {
        return confBuilder;
    }
}
