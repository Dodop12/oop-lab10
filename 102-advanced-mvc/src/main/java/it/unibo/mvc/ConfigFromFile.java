package it.unibo.mvc;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Reads the application settings from the configuration file provided.
 */
public final class ConfigFromFile {
    private static final String SEP = File.separator;
    private static final String FILE_PATH = System.getProperty("user.dir") + SEP + "102-advanced-mvc" + SEP + "src"
            + SEP + "main" + SEP + "resources" + SEP + "config.yml";
    private static final String MIN = "minimum";
    private static final String MAX = "maximum";
    private static final String ATTEMPTS = "attempts";

    private final Configuration.Builder confBuilder;

    public ConfigFromFile(final DrawNumberView... views) {
        confBuilder = new Configuration.Builder();
        try {
            final List<String> lines = Files.readAllLines(Path.of(FILE_PATH), StandardCharsets.UTF_8);

            for (final String line : lines) {
                // Splits the lines into tokens (words separated by a colon and a space by
                // default)
                final var tokenizer = new StringTokenizer(line, ": ");
                int lineNumber = 1; // Used by the error log

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
                        default -> DrawNumberApp.displayError(
                                "Configuration file format error: invalid attribute (line " + lineNumber + ")", views);
                    }
                } else {
                    DrawNumberApp
                            .displayError("Configuration file format error: each line must contain two words (line "
                                    + lineNumber + ")", views);
                }

                lineNumber++;
            }
        } catch (final IOException | NumberFormatException e) {
            e.printStackTrace();

            String message = e.getMessage();
            if (e instanceof IOException) {
                message = "Cannot find the file " + message;
            } else {
                message = "Invalid configuration file format. " + message;
            }
            DrawNumberApp.displayError(message, views);
        }
    }

    public Configuration.Builder getConfBuilder() {
        return confBuilder;
    }
}