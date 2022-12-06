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



    public static String getStringFromArray(int[] sudokuBoardArray){

        final int BOARD_SIZE = sudokuBoardArray.length;
        final int SIDE = (int) Math.sqrt(sudokuBoardArray.length);

        String newline = "\n";
        StringBuilder sb = new StringBuilder();

        for(int i=0; i<BOARD_SIZE; i++){

            if(sudokuBoardArray[i]==0)
                sb.append(",");
            else
                sb.append(sudokuBoardArray[i]);

            if((i+1)%SIDE==0) 
                sb.append(newline);
            
        }

        return sb.toString();

    }

}
