import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Random;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

public class Loom {
    private static final int SIZE = 32;

    private byte[][] screen = new byte[SIZE][SIZE]; // Represents the screen of colored squares

    private byte[][] grid = new byte[SIZE][SIZE];

    // Instruction Pointer
    private int pc = 0;

    private int x = 0, y = 0;

    private String code;

    private JFrame frame;
    private JPanel canvas;

    private Random random = new Random();

    Stack<Integer> stack = new Stack<>();

    // color palette
    private final Color[] colors = {
            new Color(0, 0, 0), // Black
            new Color(255, 241, 232), // White
            new Color(29, 43, 83), // Dark Blue
            new Color(126, 37, 83), // Dark Purple
            new Color(0, 135, 81), // Dark Green
            new Color(171, 82, 54), // Brown
            new Color(95, 87, 79), // Dark Gray
            new Color(194, 195, 199), // Light Gray
            new Color(255, 0, 77), // Red
            new Color(255, 163, 0), // Orange
            new Color(255, 236, 39), // Yellow
            new Color(0, 228, 54), // Green
            new Color(41, 173, 255), // Light Blue
            new Color(129, 118, 171), // Blue
            new Color(255, 119, 168), // Light Purple
            new Color(255, 204, 170), // Peach
            new Color(41, 24, 20), // Dark Brown
            new Color(17, 29, 53), // Navy Blue
            new Color(66, 33, 54), // Deep Purple
            new Color(18, 83, 89), // Teal
            new Color(116, 47, 41), // Rust Red
            new Color(73, 51, 59), // Muted Purple
            new Color(162, 136, 121), // Warm Gray
            new Color(243, 239, 125), // Pale Lime
            new Color(190, 18, 80), // Dark Pink
            new Color(255, 108, 36), // Orange Red
            new Color(168, 231, 46), // Lime Green
            new Color(0, 181, 67), // Emerald Green
            new Color(6, 90, 181), // Cobalt Blue
            new Color(117, 70, 101), // Dusky Purple
            new Color(255, 110, 89), // Coral
            new Color(255, 255, 255), // White
    };

    public Loom(String _code) {
        code = _code;
        init();
    }

    /**
     * Initializes the GUI components for the Loom interpreter.
     * Sets up a JFrame with a JPanel canvas that visually represents
     * the screen as a grid of colored squares. The frame is centered
     * on the screen, set to a specific size, and made non-resizable.
     * A KeyListener is added to the frame to enable closing it when
     * the "Esc" key is pressed. The canvas is responsible for painting
     * each cell of the grid based on the current state of the screen
     * array, using a predefined set of colors.
     */

    private void init() {
        frame = new JFrame("Loom");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load and set the icon
        try {
            File imageFile = new File("../resources/icon.png"); // adjust path as needed
            Image icon = ImageIO.read(imageFile);
            frame.setIconImage(icon);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Failed to load icon: " + e.getMessage());
        }

        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                screen[i][j] = 12;

        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (int x = 0; x < SIZE; x++) {
                    for (int y = 0; y < SIZE; y++) {
                        int colorValue = screen[x][y];
                        Color pixelColor = colors[colorValue];
                        g.setColor(pixelColor);
                        g.fillRect(x * 20, y * 20, 20, 20); // Adjust size as needed
                    }
                }
            }
        };

        frame.add(canvas);
        // Set the frame size (adjust the width and height as needed)
        frame.setSize(654, 676);

        // Center the frame on the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);

        // Make the frame non-resizable
        frame.setResizable(false);

        // Add a KeyListener to the frame to detect the "Esc" key press
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Not needed for the "Esc" key
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // Check if the "Esc" key (VK_ESCAPE) is pressed
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    frame.dispose(); // Close the frame
                    System.exit(0);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Not needed for the "Esc" key
            }
        });

        frame.setFocusable(true); // Ensure the frame has focus to capture key events

        frame.setVisible(true);
    }

    /**
     * Interprets and executes the given Loom code. The method
     * processes each character in the code, performing operations based
     * on specific symbols. It supports navigation of a 2D grid using
     * commands for horizontal and vertical movement, modification of cell
     * values, randomization, and screen clearing. The method also handles
     * loops and comments within the code. It uses a stack to manage
     * control flow for loops, ensuring matching brackets are properly
     * handled. Execution continues until the end of the code or an EOF
     * is encountered. Upon completion, the graphical frame is closed.
     */

    public void interpret() {
        code = cleanCode(code);
        int length = code.length();
        int count = 0;
        code += '\0';
        while (pc < length) {
            char ch = code.charAt(pc);
            // Check for EOF character and exit if found
            if (ch == '\0')
                break; // Exit the loop
            switch (ch) {
                case '>':
                case '<':
                    count = operatorCount(ch);
                    x = (ch == '>') ? (x + count) % SIZE : (x - count + SIZE) % SIZE;

                    break;
                case '^':
                case 'v':
                    count = operatorCount(ch);
                    y = (ch == '^') ? (y - count + SIZE) % SIZE : (y + count) % SIZE;

                    break;
                case '+':
                case '-':
                    count = operatorCount(ch);
                    if (ch == '+')
                        grid[x][y] = (byte) ((grid[x][y] + count) % 32);
                    else
                        grid[x][y] = (byte) ((grid[x][y] - count + SIZE) % 32);

                    break;
                case '?':
                    grid[x][y] = (byte) random.nextInt(32);
                    break;
                case 'o':
                    x = 0;
                    y = 0;
                    break;
                case 'x':
                    screen[x][y] = grid[x][y];
                    canvas.repaint();
                    // slow down the execution of the BrainFuck
                    try {
                        Thread.sleep(1); // Add a 100-millisecond delay
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case '.': // [-]
                    grid[x][y] = 0;
                    break;

                case '[':
                case '{':
                case '(':
                    int bracket = findMatchingBracket(ch, pc);
                    if (bracket == -1) {
                        frame.dispose();
                        throw new RuntimeException(
                                "Bracket matching error: Unmatched opening bracket at PC = " + pc);
                    }
                    if (grid[x][y] == 0)
                        // Skip the loop body
                        pc = bracket;
                    else
                        // Always push current position (start of loop)
                        stack.push(pc);

                    break;

                case ']':
                case '}':
                case ')':
                    if (stack.isEmpty()) {
                        frame.dispose();
                        throw new RuntimeException(
                                "Bracket matching error: Unmatched closing bracket at PC = " + pc);
                    }

                    // Loop start
                    int start = stack.peek(); // Don't pop yet
                    if (grid[x][y] != 0)
                        pc = start; // Jump back to start of loop
                    else
                        stack.pop(); // Exit loop, remove start

                    break;

                // breakpoint for debugging
                case '*':
                    System.out.println("x: " + x + ", " + "y: " + y);
                    System.out.println("Program Counter [PC]: " + (pc - 1));
                    System.out.println("Current Cell Value: " + grid[x][y]);
                    break;
                default:
                    throw new RuntimeException("Unexpected character at PC = " + pc + ": '" + ch + "'");
            }
            pc++;
        }
        // You can close the frame using frame.dispose()
        // frame.dispose();

    }

    /**
     * Cleans the given Loom code by removing whitespace and quoted
     * comments and only keeping repeatable operators, reserved
     * characters, and their associated digits. This method is used
     * to prepare the code for execution.
     *
     * @param code The Loom code to be cleaned.
     * @return The cleaned code string.
     */
    private static String cleanCode(String code) {
        StringBuilder cleaned = new StringBuilder();
        int len = code.length();
        int i = 0;

        while (i < len) {
            char ch = code.charAt(i);

            // Skip whitespace
            if (Character.isWhitespace(ch)) {
                i++;
                continue;
            }

            // Handle quoted comments
            if (ch == '\'' && i + 1 < len) {
                int next = code.indexOf('\'', i + 1);
                if (next != -1) {
                    i = next + 1;
                    continue;
                }
            }

            // If it's a repeatable operator
            if (isRepeatable(ch)) {
                cleaned.append(ch);
                i++;

                // Append all following digits
                while (i < len && Character.isDigit(code.charAt(i))) {
                    cleaned.append(code.charAt(i));
                    i++;
                }
                continue;
            }

            // If it's another reserved character, keep it
            if (isReserverd(ch))
                cleaned.append(ch);

            // Skip everything else
            i++;
        }

        return cleaned.toString();
    }

    /**
     * Checks if the given character is a reserved character in the
     * Loom language, i.e., one of the following: >, ^, v, <, +, -,
     * \*, !, ?, o, [, ], {, }, (, ).
     *
     * @param ch The character to be checked.
     * @return true if the character is a reserved character, false
     *         otherwise.
     */
    private static boolean isReserverd(char ch) {
        return "> < ^ v + - ? o x . [ ] { } ( ) *".contains(String.valueOf(ch));
    }

    /**
     * Checks if the given character is a repeatable operator in the Loom
     * language, i.e., one of the following: >, <, +, -, ^, or v.
     *
     * @param ch The character to be checked.
     * @return true if the character is a repeatable operator, false
     *         otherwise.
     */
    private static boolean isRepeatable(char ch) {
        return ch == '>' || ch == '<' || ch == '+' || ch == '-' || ch == '^' || ch == 'v';
    }

    /**
     * Checks if the given character is a digit ('0' through '9').
     *
     * @param ch The character to be checked.
     * @return true if the character is a digit, false otherwise.
     */

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * Counts consecutive occurrences of the specified operator and any
     * digit sequence following it in the code string, starting from the
     * current program counter (pc) position.
     *
     * @param op The operator character to count.
     * @return The total count of the specified operator and any digit
     *         sequence following it.
     */

    private int operatorCount(char op) {
        int count = 1;
        char next = code.charAt(pc + 1);
        int len = code.length();

        while (pc < len && (isDigit(next) || next == op)) {
            if (isDigit(next)) {
                int start = pc + 1;
                // next = code.charAt(++pc);
                while (isDigit(next)) {
                    pc++;
                    next = code.charAt(pc + 1);
                }
                count += Integer.parseInt(code.substring(start, pc + 1)) - 1;
            } else {
                while (next == op) {
                    count++;
                    pc++;
                    next = code.charAt(pc + 1);
                }
            }
        }
        return count;
    }

    /**
     * Finds the index of the matching closing bracket for a given opening bracket
     * starting from a specified position in the code string.
     *
     * @param openBracket The opening bracket character for which to find the
     *                    matching closing bracket.
     * @param start       The starting index in the code string to begin searching
     *                    for the matching bracket.
     * @return The index of the matching closing bracket in the code string, or -1
     *         if no matching bracket is found.
     */

    private int findMatchingBracket(char openBracket, int start) {
        char closeBracket = "]})".charAt("[{(".indexOf(String.valueOf(openBracket)));
        int depth = 0;
        for (int i = start; i < code.length(); i++) {
            if (code.charAt(i) == openBracket)
                depth++;
            else if (code.charAt(i) == closeBracket) {
                depth--;
                if (depth == 0)
                    return i;
            }
        }

        return -1; // Matching bracket not found
    }

}
