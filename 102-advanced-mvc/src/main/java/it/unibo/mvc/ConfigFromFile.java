package it.unibo.mvc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Reads the application settings from the configuration file provided.
 */
public final class ConfigFromFile {
    private static final String SEP = File.separator;
    private static final String FILE_PATH = System.getProperty("user.dir") + SEP + "102-advanced-mvc" + SEP + "src"
            + SEP + "main" + SEP + "resources" + SEP + "config.yml";
    // private static final String FILE_PATH =
    // Paths.get("").toAbsolutePath().resolve(SEP)
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

                // Each line of the file must contain 2 words: attribute: value (colon and space
                // between the two are needed)
                if (tokenizer.countTokens() == 2) {
                    // First word of the line; ignoring case
                    final String attribute = tokenizer.nextToken().toLowerCase();
                    // Second word of the line (must be an integer)
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
                            DrawNumberApp.displayError(
                                    "Configuration file format error: invalid attribute (line " + lineNumber + ")",
                                    views);
                            break;
                    }
                } else {
                    DrawNumberApp.displayError(
                            "Configuration file format error: lines cannot contain more than 2 words (line "
                                    + lineNumber + ")",
                            views);
                    break;
                }

                lineNumber++;
            }
        } catch (final IOException | NumberFormatException e) {
            e.printStackTrace();
            DrawNumberApp.displayError(e.getMessage(), views);
        }
    }

    public Configuration.Builder getConfBuilder() {
        return confBuilder;
    }
}