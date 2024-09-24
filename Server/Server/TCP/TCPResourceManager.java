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
                            break;

                        }
                        case AddCars -> {
                            String location = cmd_args.remove(0);
                            int count = Integer.parseInt(cmd_args.remove(0));
                            int price = Integer.parseInt(cmd_args.remove(0));
                            String success = String.valueOf(addCars(location, count, price));

                            outToClient.println(success);
                            break;
                        }
                        case AddRooms -> {
                            String location = cmd_args.remove(0);
                            int count = Integer.parseInt(cmd_args.remove(0));
                            int price = Integer.parseInt(cmd_args.remove(0));
                            String success = String.valueOf(addRooms(location, count, price));

                            outToClient.println(success);
                            break;
                        }
                        case AddCustomer -> {
                            // never used
                            // calls to this method intercepted by Middleware
                        }
                        case AddCustomerID -> {
                            int cid = Integer.parseInt(cmd_args.remove(0));
                            String success = String.valueOf(newCustomer(cid));
                            outToClient.println(success);
                            break;
                        }
                        case DeleteFlight -> {
                            int flightNumber =Integer.parseInt(cmd_args.remove(0));
                            String success = String.valueOf(deleteFlight(flightNumber));
                            outToClient.println(success);
                            break;
                        }
                        case DeleteCars -> {
                            String location = cmd_args.remove(0);
                            String success = String.valueOf(deleteCars(location));
                            outToClient.println(success);
                            break;
                        }
                        case DeleteRooms -> {
                            String location = cmd_args.remove(0);
                            String success = String.valueOf(deleteRooms(location));
                            outToClient.println(success);
                            break;
                        }
                        case DeleteCustomer -> {
                            int cid = Integer.parseInt(cmd_args.remove(0));
                            String success = String.valueOf(deleteCustomer(cid));
                            outToClient.println(success);
                            break;
                        }
                        case QueryFlight -> {
                            int flightNum = Integer.parseInt(cmd_args.remove(0));
                            String numSeatsAvail = String.valueOf(queryFlight(flightNum));

                            outToClient.println(numSeatsAvail);
                            break;
                        }
                        case QueryCars -> {
                            String location = cmd_args.remove(0);
                            String numCarsAvail = String.valueOf(queryCars(location));

                            outToClient.println(numCarsAvail);
                            break;
                        }
                        case QueryRooms -> {
                            String location = cmd_args.remove(0);
                            String numRoomsAvail = String.valueOf(queryRooms(location));

                            outToClient.println(numRoomsAvail);
                            break;
                        }
                        case QueryCustomer -> {
                            int customerID =  Integer.parseInt(cmd_args.remove(0));
                            String bill = String.valueOf(queryCustomerInfo(customerID));

                            outToClient.println(bill);
                            break;
                        }
                        case QueryFlightPrice -> {
                            int flightNum =  Integer.parseInt(cmd_args.remove(0));
                            String price = String.valueOf(queryFlightPrice(flightNum));

                            outToClient.println(price);
                            break;
                        }
                        case QueryCarsPrice -> {
                            String location = cmd_args.remove(0);
                            String price = String.valueOf(queryCarsPrice(location));

                            outToClient.println(price);
                            break;
                        }
                        case QueryRoomsPrice -> {
                            String location = cmd_args.remove(0);
                            String price = String.valueOf(queryRoomsPrice(location));

                            outToClient.println(price);
                            break;
                        }
                        case ReserveFlight -> {
                            int customerID = Integer.parseInt(cmd_args.remove(0));
                            int flightNum =  Integer.parseInt(cmd_args.remove(0));

                            String success = String.valueOf(reserveFlight(customerID,flightNum));

                            outToClient.println(success);
                            break;
                        }
                        case ReserveCar -> {
                            int customerID = Integer.parseInt(cmd_args.remove(0));
                            String location = cmd_args.remove(0);

                            String success = String.valueOf(reserveCar(customerID,location));

                            outToClient.println(success);
                            break;
                        }
                        case ReserveRoom -> {
                            int customerID = Integer.parseInt(cmd_args.remove(0));
                            String location = cmd_args.remove(0);

                            String success = String.valueOf(reserveRoom(customerID,location));

                            outToClient.println(success);
                            break;
                        }
                        case Bundle -> {
                            int customerID = Integer.parseInt(cmd_args.remove(0));
                            boolean room = Boolean.parseBoolean(cmd_args.remove(cmd_args.size()-1));
                            boolean car = Boolean.parseBoolean(cmd_args.remove(cmd_args.size()-1));
                            String location = cmd_args.remove(cmd_args.size()-1);

                            String success = String.valueOf(bundle(customerID, cmd_args, location, car, room));
                            outToClient.println(success);
                            break;
                        }
                        case Quit -> {
                            break;
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
