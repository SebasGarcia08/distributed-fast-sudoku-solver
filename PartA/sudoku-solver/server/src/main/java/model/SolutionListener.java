package model;

public interface SolutionListener {
    void solutionFound(int[] solution, int threadsUsed, Demo.CallbackPrx callback, long startTime);
}