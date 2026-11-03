import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ContactClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            ContactInterface stub = (ContactInterface) registry.lookup("ContactService");

            Scanner scanner = new Scanner(System.in);
            int choice;

            do {
                System.out.println("\n=== QUAN LY DANH BA ===");
                System.out.println("1. Them lien he");
                System.out.println("2. Tim lien he");
                System.out.println("3. Xoa lien he");
                System.out.println("4. Thoat");
                System.out.print("Chon: ");
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Nhap ten: ");
                        String name = scanner.nextLine();
                        System.out.print("Nhap so dien thoai: ");
                        String phone = scanner.nextLine();
                        stub.addContact(name, phone);
                        System.out.println("Da them lien he!");
                        break;
                    case 2:
                        System.out.print("Nhap ten can tim: ");
                        String searchName = scanner.nextLine();
                        String result = stub.findContact(searchName);
                        if (result != null) {
                            System.out.println("So dien thoai: " + result);
                        } else {
                            System.out.println("Khong tim thay lien he!");
                        }
                        break;
                    case 3:
                        System.out.print("Nhap ten can xoa: ");
                        String deleteName = scanner.nextLine();
                        if (stub.deleteContact(deleteName)) {
                            System.out.println("Da xoa lien he!");
                        } else {
                            System.out.println("Khong tim thay lien he de xoa!");
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
