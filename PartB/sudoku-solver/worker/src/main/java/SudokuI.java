import utils.SudokuConverter;
import model.Grid;
import model.SolutionListener;
import printer.Printer;

import java.io.Console;
import java.util.Arrays;
import solver.Solver;
import solver.SolverThread;

/*
 * MESSAGE FROM CLIENT
 */
public class SudokuI implements Demo.Sudoku, SolutionListener {

    public void solveSudoku(String msg, Demo.CallbackPrx callback, com.zeroc.Ice.Current current) {

        long startTime = System.currentTimeMillis();

        int[] sudokuBoardArray = SudokuConverter.getArrayFromString(msg);

        Grid grid = new Grid(sudokuBoardArray);
        Solver solver = new Solver(grid);

        SolverThread solverThread = new SolverThread(solver);
        solverThread.registerListener(this, callback, startTime);
        solverThread.setThreadsUsed(0);
        Thread thread = new Thread(solverThread);
        thread.start();
    }

    @Override
    public void solutionFound(int[] solution, int threadsUsed, Demo.CallbackPrx callback, long startTime) {

        System.out.println("\nSolved in " + (System.currentTimeMillis() - startTime) + "ms: ");
        Printer.writeSudoku(Printer.fromArrayToString(solution));

    }

}