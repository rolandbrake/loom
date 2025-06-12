public class App {
    /**
     * Example usage of the Loom class.
     *
     * Creates a Loom object with the given code string, and then calls
     * the interpret method to execute the code.
     *
     * @param args command line arguments (not used)
     * @throws Exception if an error occurs during execution
     */
    public static void main(String[] args) throws Exception {
        String code = "+{?x.+31[>?x.<[>+<-]>-]v>+}";
        Loom loom = new Loom(code);
        loom.interpret();

    }
}
