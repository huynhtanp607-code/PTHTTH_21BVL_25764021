
package Lab_4.Bai9;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Scanner;

public class MulticastReceiver {

    private static final String GROUP = "239.255.0.1";
    private static final int PORT = 9000;

    public static void main(String[] args) {

        NetworkInterface networkInterface = chonInterface();

        if (networkInterface == null) {
            System.out.println("Khong tim thay NetworkInterface phu hop.");
            return;
        }

        InetAddress group;

        try {
            group = InetAddress.getByName(GROUP);
        } catch (UnknownHostException e) {
            System.out.println("Dia chi multicast khong hop le.");
            return;
        }

        try (MulticastSocket socket = new MulticastSocket(PORT)) {

            socket.setNetworkInterface(networkInterface);

            SocketAddress groupAddress =
                    new InetSocketAddress(group, PORT);

            // Tham gia group bang NetworkInterface cu the
            socket.joinGroup(groupAddress, networkInterface);

            System.out.println("Receiver dang chay!");
            System.out.println("Group: " + GROUP);
            System.out.println("Port: " + PORT);
            System.out.println("Interface: "
                    + networkInterface.getName());
            System.out.println("Dang cho thong diep...");

            try {
                while (true) {
                    byte[] buffer = new byte[2048];

                    DatagramPacket packet =
                            new DatagramPacket(buffer, buffer.length);

                    socket.receive(packet);

                    String message = new String(
                            packet.getData(),
                            packet.getOffset(),
                            packet.getLength(),
                            java.nio.charset.StandardCharsets.UTF_8);

                    System.out.println(
                            "Nhan tu " + packet.getAddress()
                            + ": " + message);
                }
            } finally {
                // Roi group truoc khi socket dong
                socket.leaveGroup(groupAddress, networkInterface);
                System.out.println("Da roi multicast group.");
            }

        } catch (IOException e) {
            System.out.println("Loi Receiver: " + e.getMessage());
        }
    }

    private static NetworkInterface chonInterface() {

        try {
            Enumeration<NetworkInterface> interfaces =
                    NetworkInterface.getNetworkInterfaces();

            java.util.List<NetworkInterface> available =
                    new java.util.ArrayList<>();

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
