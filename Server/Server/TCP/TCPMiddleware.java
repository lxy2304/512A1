package Server.TCP;

import Client.Command;
import Server.Common.Car;
import Server.Common.Flight;
import Server.Common.Room;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Vector;

public class TCPMiddleware {

    private static int port = 2324;

    private String flight_host;

    private String car_host;

    private String room_host;

    ObjectOutputStream flight_output_stream;

    BufferedReader flight_input_stream;

    ObjectOutputStream car_output_stream;

    BufferedReader car_input_stream;

    ObjectOutputStream room_output_stream;

    BufferedReader room_input_stream;

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("Wrong number of inputs");
            System.exit(1);
        }
        TCPMiddleware server= new TCPMiddleware(args[0], args[1], args[2]);
        try
        {
            //comment this line and uncomment the next one to run in multiple threads.
//            server.runServer();
            server.runServerThread();
        }
        catch (IOException e)
        { }
    }

    private void runServerThread() throws IOException{
        ServerSocket serverSocket = new ServerSocket(port); // establish a server socket to receive messages over the network from clients
        System.out.println("Server ready...");
        while (true)
        {
            Socket socket=serverSocket.accept();
            new TCPMiddlewareThread(socket, this.flight_host, this.car_host, this.room_host).start();
        }
    }

    public TCPMiddleware(String flight_host, String car_host, String room_host) {
        this.flight_host = flight_host;
        this.car_host = car_host;
        this.room_host = room_host;
    }

    public void runServer() throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(port); // establish a server socket to receive messages over the network from clients
        System.out.println("Server ready...");


        try {
            Socket flight_socket = new Socket(this.flight_host, port);
            this.flight_output_stream = new ObjectOutputStream(flight_socket.getOutputStream());
            this.flight_input_stream = new BufferedReader(new InputStreamReader(flight_socket.getInputStream()));

            Socket car_socket = new Socket(this.car_host, port);
            this.car_output_stream = new ObjectOutputStream(car_socket.getOutputStream());
            this.car_input_stream = new BufferedReader(new InputStreamReader(car_socket.getInputStream()));

            Socket room_socket = new Socket(this.room_host, port);
            this.room_output_stream = new ObjectOutputStream(room_socket.getOutputStream());
            this.room_input_stream = new BufferedReader(new InputStreamReader(room_socket.getInputStream()));

        } catch (IOException e){
            System.out.println("Socket creation failed due to IO exception");
        }
        while (true) // runs forever
        {
            Socket socket=serverSocket.accept(); // listen for a connection to be made to this socket and accept it
            try
            {
                ObjectInputStream client_input_stream = new ObjectInputStream(socket.getInputStream());
                PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true);
                while (true) {
                    execute((Vector<String>) client_input_stream.readObject(), outToClient);
                }
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
                cmd_args.set(0,Command.AddCustomerID.name());
                cmd_args.add(cid+"");
                flight_output_stream.writeObject(cmd_args);
                String s1 = flight_input_stream.readLine();
                room_output_stream.writeObject(cmd_args);
                String s2 = room_input_stream.readLine();
                car_output_stream.writeObject(cmd_args);
                String s3 = car_input_stream.readLine();

                if (s1.equals(s2) && s2.equals(s3) && s3.equals("true")) {
                    outToClient.println(cid);
                } else {
                    System.out.println("Failed to add customer.");
                    outToClient.println("false");
                }
                break;
            }
            case AddCustomerID -> {
                flight_output_stream.writeObject(cmd_args);
                String s1 = flight_input_stream.readLine();
                if (s1.equals("true")) {
                    room_output_stream.writeObject(cmd_args);
                    String s2 = room_input_stream.readLine();
                    if (s2.equals("true")) {
                        car_output_stream.writeObject(cmd_args);
                        String s3 = car_input_stream.readLine();
                        if (s3.equals("true")) {
                            outToClient.println("true");
                        } else {
                            //revert flight and room customer creation
                            Vector<String> v = new Vector<>();
                            v.add(Command.DeleteCustomer.name());
                            v.add(cmd_args.get(1));
                            flight_output_stream.writeObject(v);
                            flight_input_stream.readLine();
                            room_output_stream.writeObject(v);
                            room_input_stream.readLine();
                            System.out.println("Add new customer to Car server failed");
                            outToClient.println("false");
                        }
                    } else {
                        //revert flight customer creation
                        Vector<String> v = new Vector<>();
                        v.add(Command.DeleteCustomer.name());
                        v.add(cmd_args.get(1));
                        flight_output_stream.writeObject(v);
                        flight_input_stream.readLine();
                        System.out.println("Add new customer to Room server failed");
                        outToClient.println("false");
                    }
                } else {
                    System.out.println("Add new customer to Flight server failed");
                    outToClient.println("false");
                }
                break;
            }
            case DeleteCustomer -> {
                flight_output_stream.writeObject(cmd_args);
                String s1 = flight_input_stream.readLine();
                room_output_stream.writeObject(cmd_args);
                String s2 = room_input_stream.readLine();
                car_output_stream.writeObject(cmd_args);
                String s3 = car_input_stream.readLine();
                if (s1.equals(s2) && s2.equals(s3) && s3.equals("true")) {
                    outToClient.println("true");
                } else {
                    System.out.println("execution failed");
                    outToClient.println("false");
                }
                break;
            }
            case QueryCustomer -> {
                outToClient.println("Bill for customer " + cmd_args.get(1));
                String tmp;
                flight_output_stream.writeObject(cmd_args);
                flight_input_stream.readLine();
                while (!(tmp = flight_input_stream.readLine()).equals("end")){
                    outToClient.println(tmp);
                }
                room_output_stream.writeObject(cmd_args);
                room_input_stream.readLine();
                while (!(tmp = room_input_stream.readLine()).equals("end")){
                    outToClient.println(tmp);
                }
                car_output_stream.writeObject(cmd_args);
                car_input_stream.readLine();
                while (!(tmp = car_input_stream.readLine()).equals("end")){
                    outToClient.println(tmp);
                }
                outToClient.println("end");
                break;
            }
            case Bundle -> {
                Boolean room = Boolean.parseBoolean(cmd_args.get(cmd_args.size()-1));
                Boolean car = Boolean.parseBoolean(cmd_args.get(cmd_args.size()-2));

                if (car) {
                    Vector<String> reserve_car = new Vector<>();
                    reserve_car.add(Command.ReserveCar.name());
                    reserve_car.add(cmd_args.get(1));
                    reserve_car.add(cmd_args.get(cmd_args.size()-3));
                    car_output_stream.writeObject(reserve_car);
                    Boolean car_res = Boolean.parseBoolean(car_input_stream.readLine());
                    if (!car_res) {
                        outToClient.println("false");
                        break;
                    }
                }

                if (room) {
                    Vector<String> reserve_room = new Vector<>();
                    reserve_room.add(Command.ReserveRoom.name());
                    reserve_room.add(cmd_args.get(1));
                    reserve_room.add(cmd_args.get(cmd_args.size()-3));
                    room_output_stream.writeObject(reserve_room);
                    Boolean room_res = Boolean.parseBoolean(room_input_stream.readLine());
                    if (!room_res) {
                        if (car) {
                            // if room reservation fails and car is already reserved, unreserve car
                            Vector<String> cancelCar = new Vector<>();
                            cancelCar.add("unReserveItem");
                            cancelCar.add(cmd_args.get(1));
                            cancelCar.add(Car.getKey(cmd_args.get(cmd_args.size()-3)));
                            cancelCar.add(cmd_args.get(cmd_args.size()-3));
                            car_output_stream.writeObject(cancelCar);

                        }
                        outToClient.println("false");
                        break;
                    }
                }

                cmd_args.set(cmd_args.size()-1, "false");
                cmd_args.set(cmd_args.size()-2, "false");
                flight_output_stream.writeObject(cmd_args);
                Boolean flight_res = Boolean.parseBoolean(flight_input_stream.readLine());
                if (!flight_res) {
                    if (car) {
                        // if flight reservation fails and car is already reserved, unreserve car
                        Vector<String> cancelCar = new Vector<>();
                        cancelCar.add("unReserveItem");
                        cancelCar.add(cmd_args.get(1));
                        cancelCar.add(Car.getKey(cmd_args.get(cmd_args.size()-3)));
                        cancelCar.add(cmd_args.get(cmd_args.size()-3));
                        car_output_stream.writeObject(cancelCar);
                    }
                    if (room) {
                        // if flight reservation fails and room is already reserved, unreserve room
                        Vector<String> cancelRoom = new Vector<>();
                        cancelRoom.add("unReserveItem");
                        cancelRoom.add(cmd_args.get(1));
                        cancelRoom.add(Room.getKey(cmd_args.get(cmd_args.size()-3)));
                        cancelRoom.add(cmd_args.get(cmd_args.size()-3));
                        room_output_stream.writeObject(cancelRoom);
                    }
                    outToClient.println("false");
                    break;
                }
                outToClient.println("true");
            }

            case Quit -> {
                break;
            }
        }
    }
}

