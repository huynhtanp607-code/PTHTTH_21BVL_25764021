import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuctionServer extends UnicastRemoteObject implements AuctionInterface {

    private Map<String, Double> products;
    private Map<String, Double> currentBids;
    private Map<String, String> bidders;

    protected AuctionServer() throws RemoteException {
        super();
        products = new HashMap<>();
        currentBids = new HashMap<>();
        bidders = new HashMap<>();
    }

    @Override
    public void addProduct(String productName, double startPrice) throws RemoteException {
        products.put(productName, startPrice);
        currentBids.put(productName, startPrice);
        bidders.put(productName, "None");
        System.out.println("Added product: " + productName + " with start price: " + startPrice);
    }

    @Override
    public List<String> viewProducts() throws RemoteException {
        List<String> productList = new ArrayList<>();
        for (String product : products.keySet()) {
            double startPrice = products.get(product);
            double currentBid = currentBids.get(product);
            String bidder = bidders.get(product);
            productList.add(product + " - Start: " + startPrice + ", Current Bid: " + currentBid + ", Bidder: " + bidder);
        }
        return productList;
    }

    @Override
    public boolean placeBid(String productName, double bidAmount, String bidder) throws RemoteException {
        if (products.containsKey(productName)) {
            double currentBid = currentBids.get(productName);
            if (bidAmount > currentBid) {
                currentBids.put(productName, bidAmount);
                bidders.put(productName, bidder);
                System.out.println("New bid on " + productName + ": " + bidAmount + " by " + bidder);
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            AuctionServer server = new AuctionServer();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("AuctionService", server);
            System.out.println("AuctionServer is running...");
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
