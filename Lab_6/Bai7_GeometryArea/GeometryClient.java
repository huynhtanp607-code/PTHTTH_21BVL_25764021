import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class GeometryClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            GeometryInterface stub = (GeometryInterface) registry.lookup("GeometryService");

            Scanner scanner = new Scanner(System.in);
            int choice;

            do {
                System.out.println("\n=== TINH DIEN TICH HINH HOC ===");
                System.out.println("1. Hinh chu nhat");
                System.out.println("2. Hinh tron");
                System.out.println("3. Hinh tam giac");
                System.out.println("4. Thoat");
                System.out.print("Chon: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.print("Nhap chieu rong: ");
                        double width = scanner.nextDouble();
                        System.out.print("Nhap chieu cao: ");
                        double height = scanner.nextDouble();
                        double rectArea = stub.rectangleArea(width, height);
                        System.out.println("Dien tich hinh chu nhat: " + rectArea);
                        break;
                    case 2:
                        System.out.print("Nhap ban kinh: ");
                        double radius = scanner.nextDouble();
                        double circleArea = stub.circleArea(radius);
                        System.out.println("Dien tich hinh tron: " + circleArea);
                        break;
                    case 3:
                        System.out.print("Nhap canh day: ");
                        double base = scanner.nextDouble();
                        System.out.print("Nhap chieu cao: ");
                        double triHeight = scanner.nextDouble();
                        double triArea = stub.triangleArea(base, triHeight);
                        System.out.println("Dien tich hinh tam giac: " + triArea);
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
