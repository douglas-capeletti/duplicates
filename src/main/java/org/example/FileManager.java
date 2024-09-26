package org.example;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class FileManager {

    public static final String SEPARATOR = ",";

    public List<InputData> loadFile(String filename) throws NullPointerException {
        List<InputData> lines;
        String fullPath = Objects.requireNonNull(this.getClass().getClassLoader().getResource(filename)).getFile();
        try (Stream<String> stream = Files.lines(Paths.get(fullPath))) {
            lines = stream
                    .skip(1) // skipping the header
                    .map(inputLineParser()) // converting the lines
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while loading file: " + fullPath);
        }
        return lines;
    }

    private Function<String, InputData> inputLineParser() {
        return (line) -> {
            String[] lineParts = line.split(SEPARATOR);
            if(lineParts.length > 5) {
                return newInputData(lineParts);
            } else {
                return newInputData(Arrays.copyOf(lineParts, 6));
            }

        };
    }

    private static InputData newInputData(String[] lineParts) {
        return new InputData(
                Integer.parseInt(lineParts[0]),
                lineParts[1],
                lineParts[2],
                lineParts[3],
                lineParts[4],
                lineParts[5]
        );
    }

    public void storeFile(String filename, List<OutputData> data) {
        storeFile(
                filename,
                data,
                (out) -> out.source() + SEPARATOR + out.Match() + SEPARATOR + (out.accurate() ? "High" : "Low") + "\n",
                "ContactID Source" + SEPARATOR + "ContactID Match" + SEPARATOR + "Accuracy" + "\n"
        );
    }

    public <T> void storeFile(String filename, List<T> data, Function<T, String> lineParser, String headerLine) {
        String fullPath = Paths.get(filename).getFileName().toString();
        try {
            FileOutputStream outputStream = new FileOutputStream(fullPath);
            // Writing the header
            if (headerLine != null) {
                outputStream.write(headerLine.getBytes(StandardCharsets.UTF_8));
            }
            // writing the output lines
            for (T line : data) {
                String x = lineParser.apply(line);
                outputStream.write(x.getBytes(StandardCharsets.UTF_8));
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while saving file: " + fullPath);
        }
    }
}
