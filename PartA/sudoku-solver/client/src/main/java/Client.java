import java.io.*;

public class Client {

    public static final String INPUT_PATH = "./client/src/main/resources/input.txt";
    private static String hostname = "";

    public static void main(String[] args) {

        java.util.List<String> extraArgs = new java.util.ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "config.client",
                extraArgs)) {

            // Printer configuration
            Demo.SudokuPrx twoway = Demo.SudokuPrx.checkedCast(communicator.propertyToProxy("Printer.Proxy")).ice_twoway().ice_secure(false);
            Demo.SudokuPrx printer = twoway.ice_twoway();

            if (printer == null) {
                throw new Error("Invalid proxy");
            }

            // Callback configuration
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Callback");
            com.zeroc.Ice.Object obj = new Callback();
            com.zeroc.Ice.ObjectPrx objectPrx = adapter.add(obj, com.zeroc.Ice.Util.stringToIdentity("callback"));
            adapter.activate();
            Demo.CallbackPrx callPrx = Demo.CallbackPrx.uncheckedCast(objectPrx);

            hostname = getHostname("hostname");

            run(printer, callPrx);

        }
    }


    public static void run(Demo.SudokuPrx printer, Demo.CallbackPrx callPrx) {

        System.out.println("\n-------------------------------------------------- \n");
        System.out.println("HELLO " + hostname+"\nThe sudoku written in"+INPUT_PATH+" is being loaded...\n");

       
        String sudoku = loadSudoku();
        int side = sudoku.indexOf("\n");

        System.out.println("INPUT:\n\n"+sudoku+"\nWith a side length of "+side);
        System.out.println("\n-------------------------------------------------- \n");

        printer.solveSudoku(sudoku, callPrx);




    }

  
    public static String getHostname(String m) {

        String str = null, output = "";

        InputStream s;
        BufferedReader r;

        try {
            Process p = Runtime.getRuntime().exec(m);

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((str = br.readLine()) != null)
                output += str + System.getProperty("line.separator");
            br.close();
            return output;
        } catch (Exception ex) {
        }

        return output;
    }

    public static String loadSudoku(){

        String sudoku = "";

        File file = new File(INPUT_PATH);

        try{

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
          
            while ((line = reader.readLine()) != null){

                sudoku+=line+'\n';

            }


        }catch(FileNotFoundException fileNotFoundException){
            System.out.println("ERROR: The file input.txt was not found");
        }catch(IOException ioException){

        }

            
        

        return sudoku;
    }


      
       


}
