import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashSet;

public class FileManager {

    public static boolean getOrCreateDirectory(Path path) {
        try {
            Files.createDirectory(path);
            return true;
        } catch (FileAlreadyExistsException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean getOrCreateFile(Path path) {
        try {
            Files.createFile(path);
            return true;
        } catch (FileAlreadyExistsException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteDirectory(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteDirectory(Path path, boolean deleteRecursively) {
        if (!deleteRecursively) return FileManager.deleteDirectory(path);
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Files.exists(path);
    }

    public static boolean clearDirectory(Path path, boolean deleteRecursively) {
        // perform operations
        try {
            Files.walkFileTree(path, null, deleteRecursively ? Integer.MAX_VALUE : 0, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        // check result
        try {
            Files.walkFileTree(path, null, deleteRecursively ? Integer.MAX_VALUE : 0, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    throw new DirectoryNotEmptyException(file.toString());
                }
            });
            return true;
        } catch (DirectoryNotEmptyException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Collection<Path> getSubDirectories(Path path) {
        Collection<Path> subDirectories = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path dir : stream)
                subDirectories.add(dir);
            return subDirectories;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject readJson(Path path) {
        try {
            byte[] metadataBytes = Files.readAllBytes(path);
            String metadataStr = new String(metadataBytes);
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(metadataStr);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeJson(Path path, JSONObject jsonObject) {
        try {
            byte[] metadataBytes = jsonObject.toString().getBytes();
            Files.write(path, metadataBytes);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static CSVParser readCsv(Path path, String... columns) {
        try {
            Reader in = Files.newBufferedReader(path);
            return new CSVParser(in, CSVFormat.DEFAULT.withHeader(columns));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CSVPrinter writeCsv(Path path, String... columns) {
        try {
            Writer out = Files.newBufferedWriter(path);
            return new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(columns));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CSVPrinter appendCsv(Path path, String... columns) {
        try {
            Writer out = Files.newBufferedWriter(path, StandardOpenOption.APPEND);
            return new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(columns));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

