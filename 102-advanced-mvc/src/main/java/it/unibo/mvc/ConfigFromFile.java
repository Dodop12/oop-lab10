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
    private static final String MIN = "minimum";
    private static final String MAX = "maximum";
    private static final String ATTEMPTS = "attempts";

    private static final String DEFAULT_VALUES_SET = "Default values have been set.";
    private static final String FILE_NOT_FOUND_ERROR = "Cannot find the file '%s'.";
    private static final String FILE_READ_ERROR = "Cannot read the file '%s'.";
    private static final String FILE_FORMAT_ERROR = "Configuration file format error: %s (line %d).";
    private static final String FILE_PATH_ERROR = "Cannot read the file path: %s.";

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

            if (Objects.isNull(fileURL)) { // File not found
                DrawNumberApp.displayErrorAll(String.format(FILE_NOT_FOUND_ERROR, FILE_NAME) + " " + DEFAULT_VALUES_SET,
                        views);
            } else {
                final Path filePath = Path.of(fileURL.toURI());

                final List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
                for (final String line : lines) {
                    readLine(lineNumber, line, views);
                    lineNumber++;
                }
            }
        } catch (final IOException e) { // File cannot be read
            DrawNumberApp.displayErrorAll(String.format(FILE_READ_ERROR, FILE_NAME) + " " + DEFAULT_VALUES_SET, views);
        } catch (final NumberFormatException e) {
            DrawNumberApp.displayErrorAll(
                    String.format(FILE_FORMAT_ERROR, e.getMessage(), lineNumber) + " " + DEFAULT_VALUES_SET, views);
        } catch (final URISyntaxException e) { // Error while reading the file path
            DrawNumberApp.displayErrorAll(String.format(FILE_PATH_ERROR, e.getMessage()) + " " + DEFAULT_VALUES_SET,
                    views);
        }
    }

    /**
     * Reads the attribute of the given line and sets the corrisponding value
     * 
     * @param lineNumber number of current line
     * @param line       line of the file to read
     * @param views      views to attach
     * 
     * @throws NumberFormatException if the value field does not contain a number
     */
    private void readLine(int lineNumber, final String line, final DrawNumberView... views)
            throws NumberFormatException {
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
                        String.format(FILE_FORMAT_ERROR, "invalid attribute", lineNumber), views);
            }
        } else {
            DrawNumberApp.displayErrorAll(
                    String.format(FILE_FORMAT_ERROR, "each line must contain exactly two words", lineNumber), views);
        }
    }

    /**
     * @return the configuration builder
     */
    public Configuration.Builder getConfBuilder() {
        return confBuilder;
    }
}
