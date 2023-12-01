package it.unibo.mvc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
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
     * @param views the graphical interfaces of the app
     */
    public ConfigFromFile(final DrawNumberView... views) {
        confBuilder = new Configuration.Builder();
        int lineNumber = 1; // Used by the error log

        try {
            // Searches for the specified file in the class path and gets its URL
            final var fileURL = ClassLoader.getSystemResource(FILE_NAME);
            if (Objects.isNull(fileURL)) {
                displayFileNotFoundError(FILE_NAME, views);
            } else {
                readFile(fileURL, lineNumber, views);
            }
        } catch (final URISyntaxException e) {
            displayPathError(e.getMessage(), views);
        } catch (final IOException e) {
            displayReadFileError(FILE_NAME, views);
        } catch (final NumberFormatException e) {
            displayFormatError(e.getMessage(), lineNumber, views);
        }
    }

    /**
     * Reads the given file to extract the values.
     * 
     * @param fileURL    the URL of the file to read
     * @param lineNumber the line counter of the file (used for error logs)
     * @param views      the graphical interfaces of the app
     * @throws IOException           if the file cannot be read
     * @throws NumberFormatException if the format of the value fields are incorrect
     * @throws URISyntaxException    if the format of the file URL is incorrect and
     *                               cannot be converted to an URI
     */
    private void readFile(final URL fileURL, int lineNumber, final DrawNumberView... views)
            throws URISyntaxException, IOException, NumberFormatException {
        final Path filePath = Path.of(fileURL.toURI());

        final List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        for (final String line : lines) {
            readLine(line, lineNumber, views);
            lineNumber++;
        }
    }

    /**
     * Reads the attribute of the given line of the file and sets the corrisponding
     * value.
     * 
     * @param line       the line of the file to read
     * @param lineNumber the number of current line
     * @param views      the graphical interfaces of the app
     * 
     * @throws NumberFormatException if the value field does not contain a number
     */
    private void readLine(final String line, int lineNumber, final DrawNumberView... views)
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
                default -> displayFormatError("invalid attribute", lineNumber, views);
            }
        } else {
            displayFormatError("each line must contain exactly two words", lineNumber, views);
        }
    }

    /**
     * Shows the error caused by the absence of the given file in the current
     * classpath.
     * It also informs the user that as a result default values have been set.
     * 
     * @param fileName the name of the specified file
     * @param views    the graphical interfaces where the error has to be displayed
     */
    private void displayFileNotFoundError(final String fileName, final DrawNumberView... views) {
        DrawNumberApp.displayErrorAll(String.format(FILE_NOT_FOUND_ERROR, fileName) + " " + DEFAULT_VALUES_SET, views);
    }

    /**
     * Shows the error occurred while trying to read the URL of the file before
     * converting it into an URI and then into a path.
     * It also informs the user that as a result default values have been set.
     * 
     * @param cause the details of the cause
     * @param views the graphical interfaces where the error has to be displayed
     */
    private void displayPathError(final String cause, final DrawNumberView... views) {
        DrawNumberApp.displayErrorAll(String.format(FILE_PATH_ERROR, cause) + " " + DEFAULT_VALUES_SET, views);
    }

    /**
     * Shows the error occurred while trying to read the file.
     * It also informs the user that as a result default values have been set.
     * 
     * @param fileName the name of the specified file
     * @param views    the graphical interfaces where the error has to be displayed
     */
    private void displayReadFileError(final String fileName, final DrawNumberView... views) {
        DrawNumberApp.displayErrorAll(String.format(FILE_READ_ERROR, fileName) + " " + DEFAULT_VALUES_SET, views);
    }

    /**
     * Shows the error caused by an invalid format of the file.
     * It also informs the user that as a result default values have been set.
     * 
     * @param cause      the details of the cause
     * @param lineNumber the number of the line in the file which caused the error
     * @param views      the graphical interfaces where the error has to be
     *                   displayed
     */
    private void displayFormatError(final String cause, final int lineNumber, final DrawNumberView... views) {
        DrawNumberApp.displayErrorAll(String.format(FILE_FORMAT_ERROR, cause, lineNumber) + " " + DEFAULT_VALUES_SET,
                views);
    }

    /**
     * @return the configuration builder
     */
    public Configuration.Builder getConfBuilder() {
        return confBuilder;
    }
}
