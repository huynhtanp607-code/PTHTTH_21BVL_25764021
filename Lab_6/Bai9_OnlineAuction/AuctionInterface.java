import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface AuctionInterface extends Remote {
    void addProduct(String productName, double startPrice) throws RemoteException;
    List<String> viewProducts() throws RemoteException;
    boolean placeBid(String productName, double bidAmount, String bidder) throws RemoteException;
}
