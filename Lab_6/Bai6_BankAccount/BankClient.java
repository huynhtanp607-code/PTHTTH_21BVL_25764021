import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class BankClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            BankInterface stub = (BankInterface) registry.lookup("BankService");

            Scanner scanner = new Scanner(System.in);
            int choice;

            do {
                System.out.println("\n=== QUAN LY TAI KHOAN NGAN HANG ===");
                System.out.println("1. Kiem tra so du");
                System.out.println("2. Gui tien");
                System.out.println("3. Rut tien");
                System.out.println("4. Thoat");
                System.out.print("Chon: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.println("So du hien tai: " + stub.getBalance());
                        break;
                    case 2:
                        System.out.print("Nhap so tien gui: ");
                        double depositAmount = scanner.nextDouble();
                        stub.deposit(depositAmount);
                        System.out.println("Gui tien thanh cong!");
                        System.out.println("So du moi: " + stub.getBalance());
                        break;
                    case 3:
                        System.out.print("Nhap so tien rut: ");
                        double withdrawAmount = scanner.nextDouble();
                        if (stub.withdraw(withdrawAmount)) {
                            System.out.println("Rut tien thanh cong!");
                            System.out.println("So du moi: " + stub.getBalance());
                        } else {
                            System.out.println("Rut tien that bai! So du khong du.");
                        }
                        break;
                    case 4:
                        System.out.println("Tam biet!");
                        break;
                    default:
                        System.out.println("Lua chon khong hop le!");
                }
            } while (choice != 4);

            scanner.close();
        } catch (Exception e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
