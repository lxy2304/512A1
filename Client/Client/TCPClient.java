package Client;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.*;

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
        /*
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

         */
for(String s: arguments) {
			System.out.println(s);
		}
        switch (cmd){
            case Help -> {
                if (arguments.size() == 1) {
                    System.out.println(Command.description());
                } else if (arguments.size() == 2) {
                    Command l_cmd = Command.fromString((String)arguments.elementAt(1));
                    System.out.println(l_cmd.toString());
                } else {
                    System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0mImproper use of help command. Location \"help\" or \"help,<CommandName>\"");
                }
                break;
            }
            case AddFlight -> {
                checkArgumentsCount(4, arguments.size());

                System.out.println("Adding a new flight ");
                System.out.println("-Flight Number: " + arguments.elementAt(1));
                System.out.println("-Flight Seats: " + arguments.elementAt(2));
                System.out.println("-Flight Price: " + arguments.elementAt(3));

                if (executeSubroutine(cmd, arguments).equals("true"))
                    System.out.println("Flight added");
                else
                    System.out.println("Flight could not be added");

                break;
            }
            case AddCars -> {
                checkArgumentsCount(4, arguments.size());

                System.out.println("Adding new cars");
                System.out.println("-Car Location: " + arguments.elementAt(1));
                System.out.println("-Number of Cars: " + arguments.elementAt(2));
                System.out.println("-Car Price: " + arguments.elementAt(3));

                if (executeSubroutine(cmd, arguments).equals("true")) {
                    System.out.println("Cars added");
                } else {
                    System.out.println("Cars could not be added");
                }
                break;
            }
            case AddRooms -> {
                checkArgumentsCount(4, arguments.size());

                System.out.println("Adding new rooms");
                System.out.println("-Room Location: " + arguments.elementAt(1));
                System.out.println("-Number of Rooms: " + arguments.elementAt(2));
                System.out.println("-Room Price: " + arguments.elementAt(3));

                if (executeSubroutine(cmd, arguments).equals("true")) {
                    System.out.println("Rooms added");
                } else {
                    System.out.println("Rooms could not be added");
                }
                break;
            }
            case AddCustomer -> {
                checkArgumentsCount(1, arguments.size());

                System.out.println("Adding a new customer:=");

                System.out.println("Add customer ID: " + executeSubroutine(cmd, arguments));
                break;
            }
            case AddCustomerID -> {
                checkArgumentsCount(2, arguments.size());

                System.out.println("Adding a new customer");
                System.out.println("-Customer ID: " + arguments.elementAt(1));

                int customerID = toInt(arguments.elementAt(1));

                if (executeSubroutine(cmd, arguments).equals("true")) {
                    System.out.println("Add customer ID: " + customerID);
                } else {
                    System.out.println("Customer could not be added");
                }
                break;
            }
            case DeleteFlight -> {
                checkArgumentsCount(2, arguments.size());

                System.out.println("Deleting a flight");
                System.out.println("-Flight Number: " + arguments.elementAt(1));

                if (executeSubroutine(cmd, arguments).equals("true")) {
                    System.out.println("Flight Deleted");
                } else {
                    System.out.println("Flight could not be deleted");
                }
                break;
            }
            case DeleteCars -> {
                checkArgumentsCount(2, arguments.size());

                System.out.println("Deleting all cars at a particular location");
                System.out.println("-Car Location: " + arguments.elementAt(1));

                if (executeSubroutine(cmd, arguments).equals("true")) {
                    System.out.println("Cars Deleted");
                } else {
                    System.out.println("Cars could not be deleted");
                }
                break;
            }
            case DeleteRooms -> {
                checkArgumentsCount(2, arguments.size());

                System.out.println("Deleting all rooms at a particular location");
                System.out.println("-Car Location: " + arguments.elementAt(1));

                if (executeSubroutine(cmd, arguments).equals("true")) {
                    System.out.println("Rooms Deleted");
                } else {
                    System.out.println("Rooms could not be deleted");
                }
                break;
            }
            case DeleteCustomer -> {
                checkArgumentsCount(2, arguments.size());

                System.out.println("Deleting a customer from the database");
                System.out.println("-Customer ID: " + arguments.elementAt(1));

                if (executeSubroutine(cmd, arguments).equals("true")) {
                    System.out.println("Customer Deleted");
                } else {
                    System.out.println("Customer could not be deleted");
                }
                break;
            }
            case QueryFlight -> {
                checkArgumentsCount(2, arguments.size());

                System.out.println("Querying a flight");
                System.out.println("-Flight Number: " + arguments.elementAt(1));

                String seats = executeSubroutine(cmd, arguments);
                System.out.println("Number of seats available: " + seats);
                break;
            }
            case QueryCars -> {
                checkArgumentsCount(2, arguments.size());

                System.out.println("Querying cars location");
                System.out.println("-Car Location: " + arguments.elementAt(1));

                String numCars = executeSubroutine(cmd, arguments);
                System.out.println("Number of cars at this location: " + numCars);
                break;
            }
            case QueryRooms -> {
                checkArgumentsCount(2, arguments.size());

                System.out.println("Querying rooms location");
                System.out.println("-Room Location: " + arguments.elementAt(1));

                String numRoom = executeSubroutine(cmd, arguments);
                System.out.println("Number of rooms at this location: " + numRoom);
                break;
            }
            case QueryCustomer -> {
                checkArgumentsCount(2, arguments.size());

                System.out.println("Querying customer information");
                System.out.println("-Customer ID: " + arguments.elementAt(1));

                String bill = executeSubroutine(cmd, arguments);
                System.out.print(bill);
                break;
            }
            case QueryFlightPrice -> {
                checkArgumentsCount(2, arguments.size());

                System.out.println("Querying a flight price");
                System.out.println("-Flight Number: " + arguments.elementAt(1));

                String price = executeSubroutine(cmd, arguments);
                System.out.println("Price of a seat: " + price);
                break;
            }
            case QueryCarsPrice -> {
                checkArgumentsCount(2, arguments.size());

                System.out.println("Querying cars price");
                System.out.println("-Car Location: " + arguments.elementAt(1));

                String price = executeSubroutine(cmd, arguments);
                System.out.println("Price of cars at this location: " + price);
                break;
            }
            case QueryRoomsPrice -> {
                checkArgumentsCount(2, arguments.size());

                System.out.println("Querying rooms price");
                System.out.println("-Room Location: " + arguments.elementAt(1));

                String price = executeSubroutine(cmd, arguments);
                System.out.println("Price of rooms at this location: " + price);
                break;
            }
            case ReserveFlight -> {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Reserving seat in a flight");
                System.out.println("-Customer ID: " + arguments.elementAt(1));
                System.out.println("-Flight Number: " + arguments.elementAt(2));

                if (executeSubroutine(cmd, arguments).equals("true")) {
                    System.out.println("Flight Reserved");
                } else {
                    System.out.println("Flight could not be reserved");
                }
                break;
            }
            case ReserveCar -> {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Reserving a car at a location");
                System.out.println("-Customer ID: " + arguments.elementAt(1));
                System.out.println("-Car Location: " + arguments.elementAt(2));

                if (executeSubroutine(cmd, arguments).equals("true")) {
                    System.out.println("Car Reserved");
                } else {
                    System.out.println("Car could not be reserved");
                }
                break;
            }
            case ReserveRoom -> {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Reserving a room at a location");
                System.out.println("-Customer ID: " + arguments.elementAt(1));
                System.out.println("-Room Location: " + arguments.elementAt(2));

                if (executeSubroutine(cmd, arguments).equals("true")) {
                    System.out.println("Room Reserved");
                } else {
                    System.out.println("Room could not be reserved");
                }
                break;
            }
            case Bundle -> {
                if (arguments.size() < 6) {
                    System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0mBundle command expects at least 6 arguments. Location \"help\" or \"help,<CommandName>\"");
                    break;
                }

                System.out.println("Reserving an bundle");
                System.out.println("-Customer ID: " + arguments.elementAt(1));
                for (int i = 0; i < arguments.size() - 5; ++i)
                {
                    System.out.println("-Flight Number: " + arguments.elementAt(2+i));
                }
                System.out.println("-Location for Car/Room: " + arguments.elementAt(arguments.size()-3));
                System.out.println("-Book Car: " + arguments.elementAt(arguments.size()-2));
                System.out.println("-Book Room: " + arguments.elementAt(arguments.size()-1));

                if (executeSubroutine(cmd, arguments).equals("true")) {
                    System.out.println("Bundle Reserved");
                } else {
                    System.out.println("Bundle could not be reserved");
                }
                break;
            }
            case Quit -> {
                checkArgumentsCount(1, arguments.size());

                System.out.println("Quitting client");
                System.exit(0);
            }
        }
    }

    public String executeSubroutine(Command cmd, Vector<String> arguments){
        arguments.insertElementAt( cmd.name(), 0);

        try {
            this.output_stream.writeObject(arguments);
        } catch (IOException e) {
            System.out.println("Failed to write to output_stream.");
        }

	 try {
            return input_stream.readLine(); // receive the server's result via the input stream from the server
  
        } catch (IOException e) {
            System.out.println("Failed to receive response from server.");
        }
        return "false";

    }
}
