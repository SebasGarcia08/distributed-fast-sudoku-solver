package solver;

import model.Cell;
import model.Grid;
import model.SolutionListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;

import error.InvalidSudokuException;

public class SolverThread implements Runnable {
    private static final int MAX_THREADS = 1000;
    public static final int N_CPUS = Runtime.getRuntime().availableProcessors();
    private static int threadsUsed = 0;
    private Solver solver;
    private static SolutionListener listener;
    private Demo.CallbackPrx callback;
    private long startTime;
    private HashSet<Grid> solutions;
    private int maxSolutions = 1;

    public SolverThread(Solver solver) {
        this.solver = solver;
        this.solutions = new HashSet<>();
    }

    public SolverThread(Solver solver, HashSet<Grid> solutions) {
        this.solver = solver;
        this.solutions = solutions;
    }

    public SolverThread(Solver solver, HashSet<Grid> solutions, int maxSolutions) {
        this.solver = solver;
        this.solutions = solutions;
        this.maxSolutions = maxSolutions;
    }

    public static void setThreadsUsed(int threadsUsed) {
        SolverThread.threadsUsed = threadsUsed;
    }

    public void registerListener(SolutionListener solutionListener, Demo.CallbackPrx call, long stTime) {
        listener = solutionListener;
        callback = call;
        startTime = stTime;
    }

    private void notifySolution(int[] solution) {
        listener.solutionFound(solution, threadsUsed, callback, startTime);
    }

    @Override
    public void run() {

        threadsUsed++;

        boolean isSolved = solver.solve();

        if (isSolved) {
            Grid solutionFound = solver.getGrid();
            int numExistingSolutions = solutions.size();
            solutions.add(solutionFound);
            if (solutions.size() > numExistingSolutions) { // if new solution
                notifySolution(solutionFound.translateCells());
                System.out.println(solver.getGrid().toString());
                // System.out.println("Threads used: " + threadsUsed + ", solutions found: " +
                // solutions.size());
            }
        }
        if (solutions.size() >= maxSolutions) {
            // System.out.println("Max solutions reached: " + solutions.size() + ", threads
            // used: " + threadsUsed);
        } else {
            try {
                splitSolvingMultipleThreads();
            } catch (InvalidSudokuException e) {
            }
        }
    }

    private void splitSolvingMultipleThreads() {

        if (threadsUsed > MAX_THREADS)
            throw new InvalidSudokuException();

        PriorityQueue<Cell> cells = getCellsWithLowestPossibilities(N_CPUS, 3);
        int[] cellValues = solver.getGrid().translateCells();

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
                Solver solver = new Solver(forkGrid);
                SolverThread solverThread = new SolverThread(solver, solutions, maxSolutions);
                new Thread(solverThread).start();
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