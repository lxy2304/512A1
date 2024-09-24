package Client;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Vector;

public class TCPClient extends Client{
    private static String s_serverHost = "localhost";
    // recommended to hange port last digits to your group number
    private static int s_serverPort = 2324;

    private ObjectOutputStream output_stream;

    private BufferedReader input_stream;

    private static String s_rmiPrefix = "group_23_";

    public static void main(String args[])
    {
        if (args.length > 0)
        {
            s_serverHost = args[0];
        }
        if (args.length > 2)
        {
            System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUsage: java client.RMIClient [server_hostname [server_rmiobject]]");
            System.exit(1);
        }

        try {
            TCPClient client = new TCPClient();
            client.connectServer();
            client.start();
        } catch (Exception e) {

        }
    }

    public TCPClient()
    {
        super();
    }

    public void connectServer()
    {
        connectServer(s_serverHost, s_serverPort);
    }

    public void connectServer(String server, int port) {
        try {
            Socket socket = new Socket(server, port);
            this.output_stream = new ObjectOutputStream(socket.getOutputStream());
            this.input_stream = new BufferedReader(new InputStreamReader(socket.getInputStream())); // open an input stream from the server...

        } catch (IOException e){
            System.out.println("Socket creation failed due to IO exception");
        }

    }

    @Override
    public void execute(Command cmd, Vector<String> arguments) throws RemoteException, NumberFormatException {
        arguments.insertElementAt( cmd.name(), 0);

        try {
            this.output_stream.writeObject(arguments);
        } catch (IOException e) {
            System.out.println("Failed to write to output_stream.");
        }

        try {
            String res = input_stream.readLine(); // receive the server's result via the input stream from the server
            System.out.println(res);
        } catch (IOException e) {
            System.out.println("Faield to receive response from server.");
        }

    }
}
