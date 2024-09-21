package Server.Common;

import Server.Interface.IResourceManager;
import Server.RMI.RMIResourceManager;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

public class RMIMiddleware implements IResourceManager {

    private static String s_rmiPrefix = "group_23_";

    private static int port = 2324;

    Registry flight_registry;

    IResourceManager flight;

    IResourceManager room;

    IResourceManager car;

    private static String middleware_name;

    public RMIMiddleware(String name) {
        middleware_name = name;
        try {
            Registry registry = LocateRegistry.getRegistry("tr-open-20", 2324);
            flight = (IResourceManager) registry.lookup(s_rmiPrefix + "flight_rm" + port);
            registry = LocateRegistry.getRegistry("tr-open-21", 2324);
            room = (IResourceManager) registry.lookup(s_rmiPrefix + "room_rm" + port);
            registry = LocateRegistry.getRegistry("tr-open-22", 2324);
            car = (IResourceManager) registry.lookup(s_rmiPrefix + "car_rm" + port);
        } catch (Exception e) {
            System.out.print("error!");
        }
    }


    public static void main(String args[])
    {
        if (args.length > 0)
        {
            middleware_name = args[0];
        }

        // Create the RMI server entry
        try {
            // Create a new Server object
            RMIMiddleware server = new RMIMiddleware(middleware_name);

            // Dynamically generate the stub (client proxy)
            IResourceManager middleware_proxy = (IResourceManager) UnicastRemoteObject.exportObject(server, 0);

            // Bind the remote object's stub in the registry; adjust port if appropriate
            Registry l_registry;
            try {
                l_registry = LocateRegistry.createRegistry(port);
            } catch (RemoteException e) {
                l_registry = LocateRegistry.getRegistry(port);
            }
            final Registry registry = l_registry;
            registry.rebind(s_rmiPrefix + middleware_name, middleware_proxy);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        registry.unbind(s_rmiPrefix + middleware_name);
                        System.out.println("'" + middleware_name + "' middleware unbound");
                    }
                    catch(Exception e) {
                        System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
                        e.printStackTrace();
                    }
                }
            });
            System.out.println("'" + middleware_name + "' resource manager server ready and bound to '" + s_rmiPrefix + middleware_name + "'");
        }
        catch (Exception e) {
            System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }

    }

    @Override
    public boolean addFlight(int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        return this.flight.addFlight(flightNum, flightSeats, flightPrice);
    }

    @Override
    public boolean addCars(String location, int numCars, int price) throws RemoteException {
        return this.car.addCars(location, numCars, price);
    }

    @Override
    public boolean addRooms(String location, int numRooms, int price) throws RemoteException {
        return this.room.addRooms(location, numRooms, price);
    }

    @Override
    public int newCustomer() throws RemoteException {
        return 0;
    }

    @Override
    public boolean newCustomer(int cid) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteFlight(int flightNum) throws RemoteException {
        return this.flight.deleteFlight(flightNum);
    }

    @Override
    public boolean deleteCars(String location) throws RemoteException {
        return this.car.deleteCars(location);
    }

    @Override
    public boolean deleteRooms(String location) throws RemoteException {
        return this.room.deleteRooms(location);
    }

    @Override
    public boolean deleteCustomer(int customerID) throws RemoteException {
        return this.flight.deleteCustomer(customerID) &&
                this.car.deleteCustomer(customerID) &&
                this.room.deleteCustomer(customerID);
    }

    @Override
    public int queryFlight(int flightNumber) throws RemoteException {
        return this.flight.queryFlight(flightNumber);
    }

    @Override
    public int queryCars(String location) throws RemoteException {
        return this.car.queryCars(location);
    }

    @Override
    public int queryRooms(String location) throws RemoteException {
        return this.room.queryRooms(location);
    }

    @Override
    public String queryCustomerInfo(int customerID) throws RemoteException {
        return null;
    }

    @Override
    public int queryFlightPrice(int flightNumber) throws RemoteException {
        return this.flight.queryFlightPrice(flightNumber);
    }

    @Override
    public int queryCarsPrice(String location) throws RemoteException {
        return this.car.queryCarsPrice(location);
    }

    @Override
    public int queryRoomsPrice(String location) throws RemoteException {
        return this.room.queryRoomsPrice(location);
    }

    @Override
    public boolean reserveFlight(int customerID, int flightNumber) throws RemoteException {
        return this.flight.reserveFlight(customerID, flightNumber);
    }

    @Override
    public boolean reserveCar(int customerID, String location) throws RemoteException {
        return this.car.reserveCar(customerID, location);
    }

    @Override
    public boolean reserveRoom(int customerID, String location) throws RemoteException {
        return this.room.reserveRoom(customerID, location);
    }

    @Override
    public boolean bundle(int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException {
        return false;
    }

    @Override
    public String getName() throws RemoteException {
        return null;
    }
}
