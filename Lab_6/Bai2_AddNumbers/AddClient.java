import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class AddClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            AddInterface stub = (AddInterface) registry.lookup("AddService");

            Scanner scanner = new Scanner(System.in);
            System.out.print("Nhap so a: ");
            int a = scanner.nextInt();
            System.out.print("Nhap so b: ");
            int b = scanner.nextInt();

            int result = stub.add(a, b);
            System.out.println("Tong " + a + " + " + b + " = " + result);

            scanner.close();
        } catch (Exception e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
