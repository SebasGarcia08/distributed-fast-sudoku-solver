import utils.SudokuConverter;
import model.Grid;
import java.util.Arrays;
/*
 * MESSAGE FROM CLIENT
 */
public class SudokuI implements Demo.Sudoku {

    public void solveSudoku(String msg, Demo.CallbackPrx callback, com.zeroc.Ice.Current current) {

        System.out.println("\n"+msg);
        System.out.println(Arrays.toString(SudokuConverter.getArrayFromString(msg)));

        int[] sudokuBoardArray = SudokuConverter.getArrayFromString(msg);
        Grid grid = new Grid(sudokuBoardArray);
        //Solver solver = new Solver(grid);
        //Thread thread = new Thread(new SolverThread(solver));
       // thread.start();



    }

}