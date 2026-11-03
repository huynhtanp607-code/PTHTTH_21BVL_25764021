import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class AuctionClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            AuctionInterface stub = (AuctionInterface) registry.lookup("AuctionService");

            Scanner scanner = new Scanner(System.in);
            int choice;

            do {
                System.out.println("\n=== HE THONG DAU GIA TRUC TUYEN ===");
                System.out.println("1. Them san pham (Seller)");
                System.out.println("2. Xem danh sach san pham");
                System.out.println("3. Dat gia thau (Bidder)");
                System.out.println("4. Thoat");
                System.out.print("Chon: ");
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Nhap ten san pham: ");
                        String productName = scanner.nextLine();
                        System.out.print("Nhap gia khoi diem: ");
                        double startPrice = scanner.nextDouble();
                        stub.addProduct(productName, startPrice);
                        System.out.println("Da them san pham!");
                        break;
                    case 2:
                        System.out.println("\n=== DANH SACH SAN PHAM ===");
                        List<String> products = stub.viewProducts();
                        if (products.isEmpty()) {
                            System.out.println("Chua co san pham nao.");
                        } else {
                            for (String product : products) {
                                System.out.println(product);
                            }
                        }
                        break;
                    case 3:
                        System.out.print("Nhap ten san pham: ");
                        String bidProduct = scanner.nextLine();
                        System.out.print("Nhap gia thau: ");
                        double bidAmount = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.print("Nhap ten nguoi dat thau: ");
                        String bidder = scanner.nextLine();
                        if (stub.placeBid(bidProduct, bidAmount, bidder)) {
                            System.out.println("Dat thau thanh cong!");
                        } else {
                            System.out.println("Dat thau that bai! Gia thau phai cao hon gia hien tai.");
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
