package it.unibo.mvc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

    public ConfigFromFile(final DrawNumberView... views) {
        final var confBuilder = new Configuration.Builder();
        try {
            final List<String> lines = Files.readAllLines(Path.of(FILE_PATH), StandardCharsets.UTF_8);

            for (String string : lines) {
                var tokenizer = new StringTokenizer(string); // Splits the lines into tokens (words separated by a space
                                                             // as default)
                String current = "";
                if (tokenizer.hasMoreTokens()) {
                    current = tokenizer.nextToken();
                }

                // Assuming the attribute (min, max, attempts) is followed by the corrisponding
                // numeric value
                switch (current.toLowerCase()) {
                    case MIN:
                        confBuilder.setMin(Integer.parseInt(current));
                        break;
                    case MAX:
                        confBuilder.setMax(Integer.parseInt(current));
                        break;
                    case ATTEMPTS:
                        confBuilder.setAttempts(Integer.parseInt(current));
                        break;
                    default:
                        break;
                }
            }
        } catch (final IOException | NumberFormatException e) {
            e.printStackTrace();
            for (final var view : views) {
                view.displayError(e.getMessage());
            }
        }
    }
}