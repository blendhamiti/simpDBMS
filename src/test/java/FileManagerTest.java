import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileManagerTest {
    private static final Path root = FileSystems.getDefault().getPath("databases-test");

    // @Test
    // public void getOrCreateDirectoryAndFileTest() {
    //     Assert.assertTrue(FileManager.getOrCreateDirectory(Paths.get(root.toString(), "example-dir")));
    //     Assert.assertFalse(FileManager.getOrCreateDirectory(Paths.get(root.toString(), "example-dir")));
    //     Assert.assertTrue(FileManager.getOrCreateFile(Paths.get(root.toString(), "example-dir", "file1.txt")));
    //     Assert.assertFalse(FileManager.getOrCreateFile(Paths.get(root.toString(), "example-dir", "file1.txt")));
    // }

    @Test
    public void deleteDirectoryRecursivelyTest() {
        Path dir1 = Paths.get(root.toString(), "dir1");
        Path dir11 = Paths.get(root.toString(), "dir1", "dir11");
        Path file1 = Paths.get(root.toString(), "dir1", "file1.txt");
        Path file11 = Paths.get(root.toString(), "dir1", "dir11", "file11.txt");
        FileManager.getOrCreateDirectory(dir1);
        FileManager.getOrCreateDirectory(dir11);
        FileManager.getOrCreateFile(file1);
        FileManager.getOrCreateFile(file11);
        try {
            String text = "asda";
            Writer writer1= Files.newBufferedWriter(file1);
            Writer writer11 = Files.newBufferedWriter(file11);
            writer1.write(text);
            writer11.write(text);
            writer1.close();
            writer11.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(FileManager.deleteDirectory(dir1, true));
    }

    @Test
    public void clearDirectoryRecursivelyTest() {
        Path dir1 = Paths.get(root.toString(), "dir1");
        Path dir11 = Paths.get(root.toString(), "dir1", "dir11");
        Path file1 = Paths.get(root.toString(), "dir1", "file1.txt");
        Path file11 = Paths.get(root.toString(), "dir1", "dir11", "file11.txt");
        FileManager.getOrCreateDirectory(dir1);
        FileManager.getOrCreateDirectory(dir11);
        FileManager.getOrCreateFile(file1);
        FileManager.getOrCreateFile(file11);
        try {
            String text = "asda";
            Writer writer1= Files.newBufferedWriter(file1);
            Writer writer11 = Files.newBufferedWriter(file11);
            writer1.write(text);
            writer11.write(text);
            writer1.close();
            writer11.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(FileManager.clearDirectory(dir1));
    }



}
