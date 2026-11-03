import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class GeometryServer extends UnicastRemoteObject implements GeometryInterface {

    protected GeometryServer() throws RemoteException {
        super();
    }

    @Override
    public double rectangleArea(double width, double height) throws RemoteException {
        return width * height;
    }

    @Override
    public double circleArea(double radius) throws RemoteException {
        return Math.PI * radius * radius;
    }

    @Override
    public double triangleArea(double base, double height) throws RemoteException {
        return 0.5 * base * height;
    }

    public static void main(String[] args) {
        try {
            GeometryServer server = new GeometryServer();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("GeometryService", server);
            System.out.println("GeometryServer is running...");
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
