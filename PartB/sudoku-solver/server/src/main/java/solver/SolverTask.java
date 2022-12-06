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

public class SolverTask  {
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
        this.startTime = System.currentTimeMillis();

        this.sem = scheduler.getSemaphore();

        int[] sudokuBoardArray = SudokuConverter.getArrayFromString(sudoku);
        this.grid = new Grid(sudokuBoardArray);
    }

}