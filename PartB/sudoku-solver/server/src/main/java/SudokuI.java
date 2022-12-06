import utils.SudokuConverter;
import model.Grid;
import model.SolutionListener;
import printer.Printer;

import java.io.Console;
import java.util.Arrays;
import solver.Solver;
import solver.SolverTask;
import scheduler.Scheduler;

/*
 * MESSAGE FROM CLIENT
 */
public class SudokuI implements Demo.Sudoku, SolutionListener {

    public void solveSudoku(String msg, Demo.CallbackPrx callback, com.zeroc.Ice.Current current) {
        Scheduler scheduler = Scheduler.getInstance();
        /*
        Message formats:

        Client -> Scheduler, to request a solution:
        client:<hostname>:<sudokuBoard>

        Worker -> Scheduler, to register as a worker:
        worker:<hostname>

        Worker -> Scheduler, to send a solution:
        worker:<hostname>:<sudokuBoardSolution>

        Scheduler -> Worker, to send a task:
        <sudokuBoard>

        Scheduler -> Client, to send a solution:
        <sudokuBoardSolution>
        */

        String[] parts = msg.split(":");

        if (parts[0] == 'client') {
            if len(scheduler.getWorkers().size() == 0) {
                callback.response("No workers available. Please try again later.");
                return;
            }
            String clientHostname = parts[1].trim();
            String sudokuBoard = parts[2].trim();
            scheduler.registerClient(clientHostname, callback);
            scheduler.schedule(sudokuBoard);
        } else if (parts[0] == 'worker') {
            if (parts.length == 2) {
                String workerHostname = parts[1];
                scheduler.registerWorker(workerHostname, callback);
            } else if (parts.length == 3) {
                String sudokuBoardSolution = parts[2];
                this.solutionFound(
                    sudokuBoardSolution, 
                    0, 
                    scheduler.getClientCallback(), 
                    0
                );
            }
        }
    }

    @Override
    public void solutionFound(int[] solution, int threadsUsed, Demo.CallbackPrx callback, long startTime) {

        System.out.println("\nSolved in " + (System.currentTimeMillis() - startTime) + "ms: ");
        Printer.writeSudoku(Printer.fromArrayToString(solution));

    }

}