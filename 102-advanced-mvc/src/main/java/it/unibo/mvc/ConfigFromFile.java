package it.unibo.mvc;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Reads the application settings from the configuration file provided.
 */
public final class ConfigFromFile {
    private static final String SEP = File.separator;
    private static final String FILE_PATH = "src" + SEP + "main" + SEP + "resources" + SEP + "config.yml";
    private static final String MIN = "minimum";
    private static final String MAX = "maximum";
    private static final String ATTEMPTS = "attempts";

    private final Configuration.Builder confBuilder;

    public ConfigFromFile(final DrawNumberView... views) {
        confBuilder = new Configuration.Builder();
        try {
            final List<String> lines = Files.readAllLines(Path.of(FILE_PATH), StandardCharsets.UTF_8);

            for (final String line : lines) {
                final var tokenizer = new StringTokenizer(line); // Splits the lines into tokens (words separated by a
                                                                 // space by default)
                int lineNumber = 1; // Used by the error log

                if (tokenizer.countTokens() == 2) {
                    final String attribute = tokenizer.nextToken().toLowerCase();
                    final int value = Integer.parseInt(tokenizer.nextToken());

                    switch (attribute) {
                        case MIN:
                            confBuilder.setMin(value);
                            break;
                        case MAX:
                            confBuilder.setMax(value);
                            break;
                        case ATTEMPTS:
                            confBuilder.setAttempts(value);
                            break;
                        default:
                            displayError("Configuration file format error: invalid attribute (line " + lineNumber + ")",
                                    views);
                            break;
                    }
                } else {
                    displayError("Configuration file format error: lines cannot contain more than 2 words (line "
                            + lineNumber + ")", views);
                    break;
                }

                lineNumber++;
            }
        } catch (final IOException | NumberFormatException e) {
            e.printStackTrace();
            displayError(e.getMessage(), views);
        }
    }

    public Configuration.Builder getConfBuilder() {
        return confBuilder;
    }

    private static void displayError(final String error, final DrawNumberView... views) {
        for (final var view : views) {
            view.displayError(error);
        }
    }
}