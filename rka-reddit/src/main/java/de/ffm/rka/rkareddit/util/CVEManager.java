package de.ffm.rka.rkareddit.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CVEManager {
    private static Path pathToWrite;
    public static void main(String[] args) throws IOException {

        pathToWrite = Paths.get("C:/Skylink/jRedit/rka-reddit/target/classes/");
        String content = IOUtils.toString(Objects.requireNonNull(CVEManager.class.getResourceAsStream("/10.txt")),
                StandardCharsets.UTF_8);
        sortForCriticalCVE(content);
    }

    private static void sortForCriticalCVE(String content) throws IOException {
        List<String> critical = getLines(content);

        String criticalPath = pathToWrite.toString() + "/Critical.txt";
        Path criticalFile = Files.write(Path.of(criticalPath), critical);
        System.out.printf("path: " + criticalFile);
    }

    private static List<String> getLines(String content) {
        return Arrays.stream(content.split("\n"))
                .filter(line -> line.contains("Critical"))
                .collect(Collectors.toList());

    }
}
