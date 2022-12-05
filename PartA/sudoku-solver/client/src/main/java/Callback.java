/*
 * RESPONSE FROM THE SERVER
 */
public class Callback implements Demo.Callback {

    public void response(String s, com.zeroc.Ice.Current current){
        System.out.println("--> "+s+"\n");
    }

}
