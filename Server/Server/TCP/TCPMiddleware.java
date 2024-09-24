package Server.TCP;

import Client.Command;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Vector;

public class TCPMiddleware {

    private static int port = 2324;

    ObjectOutputStream flight_output_stream;

    BufferedReader flight_input_stream;

    ObjectOutputStream car_output_stream;

    BufferedReader car_input_stream;

    ObjectOutputStream room_output_stream;

    BufferedReader room_input_stream;

    public static void main(String[] args) {
        TCPMiddleware server= new TCPMiddleware();
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


        try {
            Socket flight_socket = new Socket("tr-open-20", port);
            this.flight_output_stream = new ObjectOutputStream(flight_socket.getOutputStream());
            this.flight_input_stream = new BufferedReader(new InputStreamReader(flight_socket.getInputStream()));

            Socket car_socket = new Socket("tr-open-19", port);
            this.car_output_stream = new ObjectOutputStream(car_socket.getOutputStream());
            this.car_input_stream = new BufferedReader(new InputStreamReader(car_socket.getInputStream()));

            Socket room_socket = new Socket("tr-open-18", port);
            this.room_output_stream = new ObjectOutputStream(room_socket.getOutputStream());
            this.room_input_stream = new BufferedReader(new InputStreamReader(room_socket.getInputStream()));

        } catch (IOException e){
            System.out.println("Socket creation failed due to IO exception");
        }
        while (true) // runs forever
        {
            String message=null;
            Socket socket=serverSocket.accept(); // listen for a connection to be made to this socket and accept it
            try
            {
                ObjectInputStream client_input_stream = new ObjectInputStream(socket.getInputStream());
                PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true);
                execute((Vector<String>) client_input_stream.readObject(), outToClient);
            }
            catch (IOException e) {} catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void execute_redirection(ObjectOutputStream o, BufferedReader bf, PrintWriter outToClient, Vector<String> cmd_args) throws IOException {
        o.writeObject(cmd_args);
        String res = bf.readLine();
        outToClient.println(res);

    }
    public void execute(Vector<String> cmd_args, PrintWriter outToClient) throws IOException {
        Command cmd = Command.fromString(cmd_args.get(0));
        switch (cmd) {
            case Help, DeleteFlight, QueryFlight, AddFlight, QueryFlightPrice, ReserveFlight -> {
                execute_redirection(this.flight_output_stream, this.flight_input_stream, outToClient, cmd_args);
                break;
            }
            case AddCars, DeleteCars, QueryCars, QueryCarsPrice, ReserveCar -> {
                execute_redirection(this.car_output_stream, this.car_input_stream, outToClient, cmd_args);
                break;
            }
            case AddRooms, DeleteRooms, QueryRooms, QueryRoomsPrice, ReserveRoom -> {
                execute_redirection(this.room_output_stream, this.room_input_stream, outToClient, cmd_args);
                break;
            }
            case AddCustomer -> {
                int cid = Integer.parseInt(String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
                        String.valueOf(Math.round(Math.random() * 100 + 1)));
                cmd_args.set(0, Command.AddCustomerID.name());
                cmd_args.add("" + cid);
                flight_output_stream.writeObject(cmd_args);
                flight_input_stream.read();
                room_output_stream.writeObject(cmd_args);
                room_input_stream.read();
                execute_redirection(this.car_output_stream, this.car_input_stream, outToClient, cmd_args);
                break;
            }
            case AddCustomerID, DeleteCustomer -> {
                flight_output_stream.writeObject(cmd_args);
                flight_input_stream.read();
                room_output_stream.writeObject(cmd_args);
                room_input_stream.read();
                execute_redirection(this.car_output_stream, this.car_input_stream, outToClient, cmd_args);
                break;
            }
            case QueryCustomer -> {
                String res = "";
                flight_output_stream.writeObject(cmd_args);
                res += flight_input_stream.readLine();
                room_output_stream.writeObject(cmd_args);
                res += room_input_stream.readLine();
                car_output_stream.writeObject(cmd_args);
                res += car_input_stream.readLine();
                outToClient.println(res);
                break;
            }
            case Bundle -> {
                Vector<String> c1 = new Vector<>(cmd_args);
                Vector<String> c2 = new Vector<>();
                Vector<String> c3 = new Vector<>();

                String res = "";

                c1.set(c1.size()-1, "false");
                c1.set(c1.size()-2, "false");
                flight_output_stream.writeObject(c1);
                res += flight_input_stream.readLine();

                int size = cmd_args.size();
                if (cmd_args.get(size-1).equals("true")) {
                    c2.add(Command.ReserveRoom.name());
                    c2.add(cmd_args.get(1));
                    c2.add(cmd_args.get(size-3));
                    room_output_stream.writeObject(c2);
                    res += room_input_stream.readLine();
                }

                if (cmd_args.get(size-2).equals("true")) {
                    c3.add(Command.ReserveCar.name());
                    c3.add(cmd_args.get(1));
                    c3.add(cmd_args.get(size-3));
                    car_output_stream.writeObject(c3);
                    res += car_input_stream.readLine();
                }

                outToClient.println(res);
                break;
            }
            case Quit -> {
                break;
            }
        }
    }
}
