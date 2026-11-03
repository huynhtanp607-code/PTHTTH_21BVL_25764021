import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class PrimeClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            PrimeInterface stub = (PrimeInterface) registry.lookup("PrimeService");

            Scanner scanner = new Scanner(System.in);
            System.out.print("Nhap so can kiem tra: ");
            int n = scanner.nextInt();

            boolean result = stub.isPrime(n);
            if (result) {
                System.out.println(n + " la so nguyen to");
            } else {
                System.out.println(n + " khong phai la so nguyen to");
            }

            scanner.close();
        } catch (Exception e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
