package Server.Common;

import Server.Interface.IResourceManager;
import Server.RMI.RMIResourceManager;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.server.UnicastRemoteObject;

public class RMIMiddleware implements Remote {

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

}
