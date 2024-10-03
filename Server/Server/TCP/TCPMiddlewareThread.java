package Server.TCP;

import Client.Command;
import Server.Common.Car;
import Server.Common.Room;
import Server.Interface.IResourceManager;

import java.io.*;
import java.net.Socket;
import java.util.Calendar;
import java.util.Vector;

public class TCPMiddlewareThread extends Thread {

    Socket socket;

    ObjectOutputStream flight_output_stream;

    BufferedReader flight_input_stream;

    ObjectOutputStream car_output_stream;

    BufferedReader car_input_stream;

    ObjectOutputStream room_output_stream;

    BufferedReader room_input_stream;

    ObjectInputStream client_input_stream;

    PrintWriter outToClient;

    public TCPMiddlewareThread(Socket socket, ObjectOutputStream flightOutputStream, BufferedReader flightInputStream, ObjectOutputStream carOutputStream, BufferedReader carInputStream, ObjectOutputStream roomOutputStream, BufferedReader roomInputStream) {
        this.socket = socket;
        this.flight_output_stream = flightOutputStream;
        this.flight_input_stream = flightInputStream;
        this.car_output_stream = carOutputStream;
        this.car_input_stream = carInputStream;
        this.room_output_stream = roomOutputStream;
        this.room_input_stream = roomInputStream;

    }

    public void run()
    {
        try
        {
            client_input_stream = new ObjectInputStream(socket.getInputStream());
            outToClient = new PrintWriter(socket.getOutputStream(),true);
            while (true) {
                Vector<String> obj = (Vector<String>) client_input_stream.readObject();
                execute(obj, outToClient);
            }
        }
        catch (IOException e) {} catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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
