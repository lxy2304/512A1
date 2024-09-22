package Client;

import java.io.IOException;
import java.net.Socket;

public class TCPClient extends Client{
    private static String s_serverHost = "localhost";
    // recommended to hange port last digits to your group number
    private static int s_serverPort = 2324;
    private static String s_serverName = "Server";

    private static Socket socket = null;


    //TODO: ADD YOUR GROUP NUMBER TO COMPILE
    private static String s_rmiPrefix = "group_23_";

    public static void main(String args[])
    {
        if (args.length > 0)
        {
            s_serverHost = args[0];
        }
        if (args.length > 1)
        {
            s_serverName = args[1];
        }
        if (args.length > 2)
        {
            System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUsage: java client.RMIClient [server_hostname [server_rmiobject]]");
            System.exit(1);
        }

        // Get a reference to the RMIRegister
        try {
            RMIClient client = new RMIClient();
            client.connectServer();
            client.start();
        }
        catch (Exception e) {
            System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public TCPClient()
    {
        super();
    }

    public void connectServer()
    {
        connectServer(s_serverHost, s_serverPort, s_serverName);
    }

    public void connectServer(String server, int port, String name) {
        try {
            socket = new Socket(server, port);


        } catch (IOException e){
            System.out.println("Socket creation failed due to IO exception");
        }

    }
}
