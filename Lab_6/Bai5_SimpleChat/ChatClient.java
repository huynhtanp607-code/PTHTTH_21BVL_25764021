import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            ChatInterface stub = (ChatInterface) registry.lookup("ChatService");

            Scanner scanner = new Scanner(System.in);
            String message;

            System.out.println("=== CHAT VOI SERVER ===");
            System.out.println("Nhap 'exit' de thoat");

            do {
                System.out.print("Client: ");
                message = scanner.nextLine();

                if (!message.equalsIgnoreCase("exit")) {
                    stub.sendMessage(message);
                    System.out.println("Server: Da nhan tin nhan cua ban!");
                }
            } while (!message.equalsIgnoreCase("exit"));

            System.out.println("Tam biet!");
            scanner.close();
        } catch (Exception e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
