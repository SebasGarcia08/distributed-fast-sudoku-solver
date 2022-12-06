package solver;

import model.Cell;
import model.Grid;
import model.SolutionListener;
import utils.SudokuConverter;
import utils.SudokuConverter;
import model.Grid;
import model.SolutionListener;
import printer.Printer;

import scheduler.Scheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Semaphore;


import error.InvalidSudokuException;

public class SolverTask implements Runnable {
    private String sudoku;
    private Grid grid;
    private Scheduler scheduler;
    private Semaphore sem;

    private static SolutionListener listener;
    private long startTime;

    public SolverTask(
        String sudoku,
        Demo.CallbackPrx callback, 
        Scheduler scheduler,
        SolutionListener listener
    ) {
        this.sudoku = sudoku;

        // Register listener
        this.listener = listener;
        this.callback = callback;
        this.startTime = System.currentTimeMillis();

        this.sem = scheduler.getSemaphore();

        int[] sudokuBoardArray = SudokuConverter.getArrayFromString(sudoku);
        this.grid = new Grid(sudokuBoardArray);
    }

    private void notifySolution(int[] solution) {
        listener.solutionFound(solution, threadsUsed, callback, startTime);
    }

    @Override
    public void run() {
        int nWorkers = scheduler.getWorkers().size();
        PriorityQueue<Cell> cells = getCellsWithLowestPossibilities(nWorkers, 9);
        int[] cellValues = this.grid.translateCells();

        Iterator<Cell> iterator = cells.iterator();

        while (iterator.hasNext()) {
            Cell cell = iterator.next();
            int x = cell.getX();
            int y = cell.getY();
            Set<Integer> possibleValues = cell.getPossibilities();
            // System.out.println("Cell " + x + ", " + y + " has " + possibleValues.size() +
            // " possibilities");
            for (int value : possibleValues) {
                // System.out.println("\tForking thread " + threadsUsed + " with value " +
                // value);
                Grid forkGrid = new Grid(cellValues);
                forkGrid.getCell(x, y).setValue(value);

                /*
                Solver solver = new Solver(forkGrid);
                SolverThread solverThread = new SolverThread(solver, solutions, maxSolutions);
                new Thread(solverThread).start();
                */
            }
        }
    }

    private PriorityQueue<Cell> getCellsWithLowestPossibilities(int n, int maxPossibilities) {

        PriorityQueue<Cell> pq = new PriorityQueue<>(n, (c1, c2) -> {
            int pcs1 = c1.getPossibilities().size();
            int pcs2 = c2.getPossibilities().size();

            if (pcs1 < pcs2)
                return -1;
            else if (pcs1 > pcs2)
                return 1;
            return 0;
        });

        int pqSize = 0;

        for (Cell cell : solver.getGrid().getCells()) {
            // Limit the size of the priority queue
            if (pqSize >= n)
                break;

            // Only add cells with more than one possibility
            if (cell.getPossibilities().size() <= 1 || cell.getPossibilities().size() > maxPossibilities)
                continue;

            pq.add(cell);
            pqSize++;
        }

        return pq;
    }

}