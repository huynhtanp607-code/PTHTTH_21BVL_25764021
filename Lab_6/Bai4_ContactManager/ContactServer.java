import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ContactServer extends UnicastRemoteObject implements ContactInterface {

    private HashMap<String, String> contacts;

    protected ContactServer() throws RemoteException {
        super();
        contacts = new HashMap<>();
    }

    @Override
    public void addContact(String name, String phone) throws RemoteException {
        contacts.put(name, phone);
        System.out.println("Added contact: " + name + " - " + phone);
    }

    @Override
    public String findContact(String name) throws RemoteException {
        return contacts.get(name);
    }

    @Override
    public boolean deleteContact(String name) throws RemoteException {
        if (contacts.containsKey(name)) {
            contacts.remove(name);
            System.out.println("Deleted contact: " + name);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            ContactServer server = new ContactServer();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("ContactService", server);
            System.out.println("ContactServer is running...");
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
