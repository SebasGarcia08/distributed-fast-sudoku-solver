module Demo
{
    interface Callback
    {
        void response(string s);
    }

    interface Sudoku
    {
        void solveSudoku(string sudoku, Callback* cl);
    }

}