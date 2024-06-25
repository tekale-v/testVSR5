import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnalyzeSchemaChanges {

    public static List<String> getMatchingFilenames(String directoryPath, List<String> keywords, List<String> considerFiles) {
        List<String> matchingFilenames = new ArrayList<>();

        File directory = new File(directoryPath);
        File[] files = directory.listFiles((dir, name) -> considerFiles.contains(name));

        if (files != null && files.length > 0) {
            for (File file : files) {
                boolean typeFound = false;

                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                    String line;
                    boolean isFirstLine = true;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (isFirstLine) {
                            isFirstLine = false;
                            continue;
                        }
                        String[] columns = line.split("\t");
                        if (columns.length > 0) {
                            String col1 = columns[0];

                            boolean keywordFound = false;

                            for (String keyword : keywords) {
                                if (col1.contains(keyword)) {
                                    keywordFound = true;
                                    break;
                                }
                            }
                            if (!keywordFound) {
                                typeFound = true;
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (typeFound) {
                    matchingFilenames.add(file.getName());
                }
            }
        }

        return matchingFilenames;
    }

    public static void main(String[] args) {
        String directoryPath = System.getenv("DirectoryPath");
        String keywordsString = System.getenv("Keywords");
        String considerFilesString = System.getenv("ConsiderFiles");

        if (directoryPath != null && keywordsString != null && considerFilesString != null) {
            List<String> keywords = Arrays.asList(keywordsString.split(","));
            List<String> considerFiles = Arrays.asList(considerFilesString.split(","));

            List<String> matchingFilenames = getMatchingFilenames(directoryPath, keywords, considerFiles);

            for (String filename : matchingFilenames) {
                System.out.println(filename);
            }
        } else {
            System.out.println("Failed to read the directory path, keywords, or considerFiles from environment variables.");
        }
    }
}
