package scheduler;

import java.util.concurrent.Semaphore;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.HashMap;

import solver.SolverTask;

import Demo.CallbackPrx;

public class Scheduler {

    private final int MAX_THREADS = Runtime.getRuntime().availableProcessors();

    private static Scheduler instance = null;

    private ExecutorService pool;
    private HashMap<String, CallbackPrx> workers;
    private String clientHostname;
    private CallbackPrx client;
    private Semaphore sem;

    // Accumulate solutions
    private HashSet<Grid> solutions;

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

    public void schedule(String sudokuBoard) {
        this.pool.execute(task);
    }

}
