import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class FlightServer extends UnicastRemoteObject implements FlightInterface {

    private Map<String, Integer> flights;

    protected FlightServer() throws RemoteException {
        super();
        flights = new HashMap<>();
        flights.put("VN001", 100);
        flights.put("VN002", 150);
        flights.put("VN003", 80);
    }

    @Override
    public boolean bookTicket(String flightNumber, int seats) throws RemoteException {
        if (flights.containsKey(flightNumber)) {
            int available = flights.get(flightNumber);
            if (available >= seats) {
                flights.put(flightNumber, available - seats);
                System.out.println("Booked " + seats + " seats for flight " + flightNumber);
                return true;
            }
        }
        return false;
    }

    @Override
    public int availableSeats(String flightNumber) throws RemoteException {
        return flights.getOrDefault(flightNumber, 0);
    }

    @Override
    public boolean cancelBooking(String flightNumber, int seats) throws RemoteException {
        if (flights.containsKey(flightNumber)) {
            int current = flights.get(flightNumber);
            flights.put(flightNumber, current + seats);
            System.out.println("Cancelled " + seats + " seats for flight " + flightNumber);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            FlightServer server = new FlightServer();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("FlightService", server);
            System.out.println("FlightServer is running...");
            System.out.println("Available flights: " + server.flights);
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
