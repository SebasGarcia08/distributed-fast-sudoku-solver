package printer;

import java.io.FileWriter;
import java.io.IOException;

public class Printer {

    public static final String OUTPUT_PATH = "./server/src/main/resources/output.txt";

    public static String fromArrayToString(int[] sudoku) {

        String horizontalSeparator = "+---+---+---+\n";
        String verticalSeparator = "|";
        String newline = "\n";

        StringBuilder sb = new StringBuilder();

        sb.append(horizontalSeparator);
        for (int i = 1; i <= 81; i++) {
            if (i % 9 == 1)
                sb.append(verticalSeparator);

            sb.append(sudoku[i - 1]);

            if (i % 3 == 0)
                sb.append(verticalSeparator);
            if (i % 9 == 0)
                sb.append(newline);
            if (i % 27 == 0)
                sb.append(horizontalSeparator);
        }

        return sb.toString();

    }

    public static void writeSudoku(String sudString) {

        try {

            FileWriter fWriter = new FileWriter(OUTPUT_PATH);
            fWriter.write(sudString);
            fWriter.close();

        } catch (IOException e) {
            System.out.print(e.getMessage());
        }

    }

}
