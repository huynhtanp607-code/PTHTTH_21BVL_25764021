
package Lab_4.Bai9;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

public class MulticastSender {

    private static final String GROUP = "239.255.0.1";
    private static final int PORT = 9000;

    public static void main(String[] args) {

        NetworkInterface networkInterface = chonInterface();

        if (networkInterface == null) {
            System.out.println("Khong tim thay interface phu hop.");
            return;
        }

        try {
            InetAddress group = InetAddress.getByName(GROUP);

            try (MulticastSocket socket = new MulticastSocket()) {

                socket.setNetworkInterface(networkInterface);
                socket.setTimeToLive(1);

                Scanner sc = new Scanner(System.in);

                System.out.println("Multicast Sender dang chay!");
                System.out.println("Group: " + GROUP);
                System.out.println("Port: " + PORT);
                System.out.println("Interface: "
                        + networkInterface.getName());
                System.out.println("Nhap noi dung thong bao.");
                System.out.println("Nhap QUIT de ket thuc.");

                while (true) {

                    System.out.print("Thong bao > ");
                    String message = sc.nextLine();

                    if (message.equalsIgnoreCase("QUIT")) {
                        break;
                    }

                    byte[] data =
                            message.getBytes(StandardCharsets.UTF_8);

                    DatagramPacket packet = new DatagramPacket(
                            data,
                            data.length,
                            group,
                            PORT);

                    socket.send(packet);

                    System.out.println("Da gui: " + message);
                }
            }

        } catch (Exception e) {
            System.out.println("Loi Sender: " + e.getMessage());
        }
    }

    private static NetworkInterface chonInterface() {

        try {
            Enumeration<NetworkInterface> interfaces =
                    NetworkInterface.getNetworkInterfaces();

            List<NetworkInterface> available = new ArrayList<>();

            System.out.println("Cac NetworkInterface:");

            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();

                try {
                    if (ni.isUp() && ni.supportsMulticast()) {
                        available.add(ni);
                        System.out.println(
                                available.size() + ". "
                                + ni.getName() + " - "
                                + ni.getDisplayName());
                    }
                } catch (SocketException ignored) {
                }
            }

            if (available.isEmpty()) {
                return null;
            }

            Scanner sc = new Scanner(System.in);
            System.out.print("Chon interface theo so thu tu: ");

            int choice = Integer.parseInt(sc.nextLine().trim());

            if (choice < 1 || choice > available.size()) {
                System.out.println("Lua chon khong hop le.");
                return null;
            }

            return available.get(choice - 1);

        } catch (Exception e) {
            System.out.println("Loi chon interface: " + e.getMessage());
            return null;
        }
    }
}
