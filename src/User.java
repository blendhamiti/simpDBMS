import java.nio.file.FileSystems;
import java.nio.file.Path;

public class User {

    public static void main(String[] args) {
        Path path = FileSystems.getDefault().getPath("data");
        Connection conn = new Connection(path);
    }
}
