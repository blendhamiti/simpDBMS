import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashSet;

public class FileManager {

    public static boolean createDirectory(Path path) {
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
        }catch (IOException e) {
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

    public static void main(String[] args) {

    }
}
