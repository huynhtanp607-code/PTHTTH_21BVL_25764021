import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class BankServer extends UnicastRemoteObject implements BankInterface {

    private double balance;

    protected BankServer() throws RemoteException {
        super();
        this.balance = 1000.0;
    }

    @Override
    public double getBalance() throws RemoteException {
        return balance;
    }

    @Override
    public void deposit(double amount) throws RemoteException {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: " + amount + ", New balance: " + balance);
        }
    }

    @Override
    public boolean withdraw(double amount) throws RemoteException {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawn: " + amount + ", New balance: " + balance);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            BankServer server = new BankServer();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("BankService", server);
            System.out.println("BankServer is running...");
            System.out.println("Initial balance: " + server.getBalance());
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
