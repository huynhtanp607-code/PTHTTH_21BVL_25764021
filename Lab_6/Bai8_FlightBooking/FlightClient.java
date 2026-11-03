import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class FlightClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            FlightInterface stub = (FlightInterface) registry.lookup("FlightService");

            Scanner scanner = new Scanner(System.in);
            int choice;

            do {
                System.out.println("\n=== HE THONG DAT VE MAY BAY ===");
                System.out.println("1. Dat ve");
                System.out.println("2. Kiem tra so ghe trong");
                System.out.println("3. Huy ve");
                System.out.println("4. Thoat");
                System.out.print("Chon: ");
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Nhap ma chuyen bay (VN001, VN002, VN003): ");
                        String flightNum = scanner.nextLine();
                        System.out.print("Nhap so ghe can dat: ");
                        int seats = scanner.nextInt();
                        if (stub.bookTicket(flightNum, seats)) {
                            System.out.println("Dat ve thanh cong!");
                        } else {
                            System.out.println("Dat ve that bai! Khong du ghe trong.");
                        }
                        break;
                    case 2:
                        System.out.print("Nhap ma chuyen bay: ");
                        String checkFlight = scanner.nextLine();
                        int available = stub.availableSeats(checkFlight);
                        System.out.println("So ghe trong cua chuyen bay " + checkFlight + ": " + available);
                        break;
                    case 3:
                        System.out.print("Nhap ma chuyen bay: ");
                        String cancelFlight = scanner.nextLine();
                        System.out.print("Nhap so ghe can huy: ");
                        int cancelSeats = scanner.nextInt();
                        if (stub.cancelBooking(cancelFlight, cancelSeats)) {
                            System.out.println("Huy ve thanh cong!");
                        } else {
                            System.out.println("Huy ve that bai!");
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
