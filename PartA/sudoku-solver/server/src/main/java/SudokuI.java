/*
 * MESSAGE FROM CLIENT
 */
public class SudokuI implements Demo.Sudoku {

    public void solveSudoku(String msg, Demo.CallbackPrx callback, com.zeroc.Ice.Current current) {

        System.out.println("\n"+msg);

    }

}