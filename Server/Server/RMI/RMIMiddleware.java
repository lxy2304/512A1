package Server.RMI;

import Server.Interface.IResourceManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

public class RMIMiddleware implements IResourceManager {

    private static String s_rmiPrefix = "group_23_";

    private static String middleware_name = "middleware";

    private static int port = 2324;

    IResourceManager flight;

    IResourceManager room;

    IResourceManager car;


    public RMIMiddleware(String host1, String host2, String host3) {
        try {
            Registry registry = LocateRegistry.getRegistry(host1, 2324);
            flight = (IResourceManager) registry.lookup(s_rmiPrefix + "Flights" );
            registry = LocateRegistry.getRegistry(host2, 2324);
            room = (IResourceManager) registry.lookup(s_rmiPrefix + "Rooms" );
            registry = LocateRegistry.getRegistry(host3, 2324);
            car = (IResourceManager) registry.lookup(s_rmiPrefix + "Cars" );
        } catch (Exception e) {
            System.out.print("Error connecting to registries.");
        }
    }


    public static void main(String args[])
    {
        if (args.length != 3) {
            System.out.print("Wrong number of arguments.");
            System.exit(1);
        }
        // Create the RMI server entry
        try {
            // Create a new Server object
            RMIMiddleware server = new RMIMiddleware(args[0], args[1], args[2]);

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
            System.out.println("'" + middleware_name + "' ready and bound to '" + s_rmiPrefix + middleware_name + "'");
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
        int cid = this.flight.newCustomer();
        this.car.newCustomer(cid);
        this.room.newCustomer(cid);
        return cid;
    }

    @Override
    public boolean newCustomer(int cid) throws RemoteException {
        return this.flight.newCustomer(cid) &&
                this.car.newCustomer(cid) &&
                this.room.newCustomer(cid);
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
        return this.car.queryCustomerInfo(customerID)+this.room.queryCustomerInfo(customerID)+this.flight.queryCustomerInfo(customerID);
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
        return this.room.bundle(customerID, new Vector<String>(), location, false, room) &&
                this.car.bundle(customerID, new Vector<String>(), location, car, false) &&
                this.flight.bundle(customerID, flightNumbers, location, false, false);
    }

    @Override
    public String getName() throws RemoteException {
        return null;
    }
}
