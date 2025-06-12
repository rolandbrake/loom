import java.nio.file.Files;
import java.nio.file.Paths;

public class App {
    /**
     * Runs Loom code loaded from a file in the examples folder.
     * 
     * @param args Command line arguments (not used).
     * @throws Exception If reading the file or execution fails.
     */
    public static void main(String[] args) throws Exception {
        // Path to the Loom code file
        String path = "../examples/rand.lm";

        // Read all bytes and convert to string
        String code = new String(Files.readAllBytes(Paths.get(path)));

        // Create Loom instance with code string
        Loom loom = new Loom(code);

        // Interpret the code
        loom.interpret();
    }
}
