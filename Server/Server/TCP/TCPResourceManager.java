package Server.TCP;

import Client.Command;
import Server.Common.ResourceManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class TCPResourceManager extends ResourceManager {

    private static int port = 2324;
    public TCPResourceManager(String p_name) {
        super(p_name);
    }

    public static void main(String[] args) {
        TCPResourceManager server= new TCPResourceManager("");
        try
        {
            //comment this line and uncomment the next one to run in multiple threads.
            server.runServer();
            //server.runServerThread();
        }
        catch (IOException e)
        { }
    }

    public void runServer() throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(port); // establish a server socket to receive messages over the network from clients
        System.out.println("Server ready...");

        while (true) // runs forever
        {
            Socket socket=serverSocket.accept(); // listen for a connection to be made to this socket and accept it
            try
            {
                ObjectInputStream inFromClient= new ObjectInputStream(socket.getInputStream());

                PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true); //PrintWriter: Prints formatted representations of objects to a text-output stream
                //socket.getOutputStream(): Returns an output stream for this socket.
                while (true) //Reads a line of text. A line is considered to be terminated by any one of a line feed ('\n'),
                {

                    Vector<String> cmd_args = (Vector<String>) inFromClient.readObject();
                    String cmd = cmd_args.remove(0);
                    switch(Command.fromString(cmd)) {
                        case Help -> {
                        }
                        case AddFlight -> {
                            int flightNumber = Integer.parseInt(cmd_args.remove(0));
                            int seats = Integer.parseInt(cmd_args.remove(0));
                            int price = Integer.parseInt(cmd_args.remove(0));

                            String success = String.valueOf(addFlight(flightNumber,seats, price));

                            outToClient.println(success);

                        }
                        case AddCars -> {
                            String location = cmd_args.remove(0);
                            int count = Integer.parseInt(cmd_args.remove(0));
                            int price = Integer.parseInt(cmd_args.remove(0));
                            String success = String.valueOf(addCars(location, count, price));

                            outToClient.println(success);
                        }
                        case AddRooms -> {
                            String location = cmd_args.remove(0);
                            int count = Integer.parseInt(cmd_args.remove(0));
                            int price = Integer.parseInt(cmd_args.remove(0));
                            String success = String.valueOf(addRooms(location, count, price));

                            outToClient.println(success);
                        }
                        case AddCustomer -> {
                            // never used
                            // calls to this method intercepted by Middleware
                        }
                        case AddCustomerID -> {
                            int cid = Integer.parseInt(cmd_args.remove(0));
                            String success = String.valueOf(newCustomer(cid));
                            outToClient.println(success);
                        }
                        case DeleteFlight -> {
                            int flightNumber =Integer.parseInt(cmd_args.remove(0));
                            String success = String.valueOf(deleteFlight(flightNumber));
                            outToClient.println(success);
                        }
                        case DeleteCars -> {
                            String location = cmd_args.remove(0);
                            String success = String.valueOf(deleteCars(location));
                            outToClient.println(success);
                        }
                        case DeleteRooms -> {
                            String location = cmd_args.remove(0);
                            String success = String.valueOf(deleteRooms(location));
                            outToClient.println(success);
                        }
                        case DeleteCustomer -> {
                            int cid = Integer.parseInt(cmd_args.remove(0));
                            String success = String.valueOf(deleteCustomer(cid));
                            outToClient.println(success);
                        }
                        case QueryFlight -> {
                        }
                        case QueryCars -> {
                        }
                        case QueryRooms -> {
                        }
                        case QueryCustomer -> {
                        }
                        case QueryFlightPrice -> {
                        }
                        case QueryCarsPrice -> {
                        }
                        case QueryRoomsPrice -> {
                        }
                        case ReserveFlight -> {
                        }
                        case ReserveCar -> {
                        }
                        case ReserveRoom -> {
                        }
                        case Bundle -> {
                        }
                        case Quit -> {

                        }
                    }
                }
            }
            catch (IOException e) {} catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
