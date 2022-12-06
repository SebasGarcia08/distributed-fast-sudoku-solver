package utils;

public class SudokuConverter {

    public static int[] getArrayFromString(String sudokuString) {

        // Removing line breaks
        sudokuString = sudokuString.replace("\n", "").replace("\r", "");

        final int BOARD_SIZE = sudokuString.length();
        int[] sudokuBoardArray = new int[BOARD_SIZE];
        char[] sudokuChars = sudokuString.toCharArray();

        for (int i = 0; i < BOARD_SIZE; i++) {

            char ch = sudokuChars[i];
            sudokuBoardArray[i] = (ch - '0' > 9 || ch - '0' < 1) ? 0 : ch - '0'; // Convert Char to Int using ASCII
                                                                                 // value

        }

        return sudokuBoardArray;

    }

    public static String getStringFromArray(int[] sudokuBoardArray) {

        // TODO 

    }

}
