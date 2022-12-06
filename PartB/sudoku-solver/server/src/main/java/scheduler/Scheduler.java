package scheduler;

import java.util.concurrent.Semaphore;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Semaphore;

import model.Cell;
import model.Grid;
import model.SolutionListener;
import utils.SudokuConverter;
import utils.SudokuConverter;
import model.Grid;
import model.SolutionListener;
import printer.Printer;

import error.InvalidSudokuException;
import Demo.CallbackPrx;

public class Scheduler {

    private final int MAX_THREADS = Runtime.getRuntime().availableProcessors();

    private static Scheduler instance = null;

    private ExecutorService pool;
    private HashMap<String, CallbackPrx> workers;
    private String clientHostname;
    private CallbackPrx client;
    private Semaphore sem;
    private long startTime;

    // Accumulate solutions
    private HashSet<Grid> solutions;
    private static SolutionListener listener;

    private Scheduler() {
        this.sem = new Semaphore(1);
        this.pool = Executors.newFixedThreadPool(MAX_THREADS);

        this.workers = new HashMap<String, CallbackPrx>();
    }

    public static Scheduler getInstance() {
        if (instance == null) {
            instance = new Scheduler();
        }
        return instance;
    }

    public ExecutorService getPool() {
        return this.pool;
    }

    public HashMap<String, CallbackPrx> getWorkers() {
        return this.workers;
    }

    public CallbackPrx getWorkerCallback(String hostname) {
        if (this.workers.containsKey(hostname)) {
            return this.workers.get(hostname);
        }
        return null;
    }

    public void registerWorker(String hostname, CallbackPrx callback) {
        try {
            this.sem.acquire();
            if (!this.workers.containsKey(hostname)) {
                this.workers.put(hostname, callback);
                System.out.println("Worker " + hostname + " joined. \n");
            }
            this.sem.release();
        } catch (InterruptedException e) {
            System.out.println("[ERROR] Worker " + hostname + " failed to join. \n");
            e.printStackTrace();
        } finally {
            this.sem.release();
        }
    }

    public void registerClient(String clientHostname, CallbackPrx callback) {
        this.clientHostname = clientHostname;
        this.client = callback;
    }

    public String getClientHostname() {
        return this.clientHostname;
    }

    public CallbackPrx getClientCallback() {
        return this.client;
    }

    public Semaphore getSemaphore() {
        return this.sem;
    }

    public void setListener(SolutionListener listener) {
        this.listener = listener;
    }

    private void notifySolution(int[] solution) {
        this.listener.solutionFound(solution, 0, client, startTime);
    }

    public void schedule(String sudokuBoard) {
        // this.pool.execute(task);
        int[] sudokuBoardArray = SudokuConverter.getArrayFromString(sudokuBoard);
        Grid grid = new Grid(sudokuBoardArray);
        this.startTime = System.currentTimeMillis();

        int nWorkers = this.workers.size();
        PriorityQueue<Cell> cells = getCellsWithLowestPossibilities(grid, nWorkers, 3);
        int[] cellValues = grid.translateCells();

        Iterator<Cell> iterator = cells.iterator();
        Iterator<HashMap.Entry<String, CallbackPrx>> workerIterator = this.workers.entrySet().iterator();

        while (iterator.hasNext() && workerIterator.hasNext()) {

            HashMap.Entry<String, CallbackPrx> entry = workerIterator.next();
            String workerHostname = entry.getKey();
            CallbackPrx workerCallback = entry.getValue();

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
                String forkGridString =  SudokuConverter.getStringFromArray(forkGrid.translateCells());
                workerCallback.response(forkGridString);
            }
        }
    }

    private PriorityQueue<Cell> getCellsWithLowestPossibilities(Grid grid, int n, int maxPossibilities) {

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

        for (Cell cell : grid.getCells()) {
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
