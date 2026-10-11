
package Lab_4.Bai10;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class DiscoveryClient {

    private static final int UDP_PORT = 9001;
    private static final int TIMEOUT_MS = 3000;

    static class ServiceInfo {
        String name;
        int tcpPort;
        String version;
        InetAddress address;

        ServiceInfo(String name, int tcpPort,
                    String version, InetAddress address) {
            this.name = name;
            this.tcpPort = tcpPort;
            this.version = version;
            this.address = address;
        }
    }

    public static void main(String[] args) {

        Map<String, ServiceInfo> services =
                new LinkedHashMap<>();

        try (DatagramSocket socket = new DatagramSocket()) {

            socket.setBroadcast(true);

            byte[] data = "DISCOVER_SERVICE".getBytes(
                    StandardCharsets.UTF_8);

            // Gui broadcast tren mang cuc bo
            DatagramPacket broadcast = new DatagramPacket(
                    data,
                    data.length,
                    InetAddress.getByName("255.255.255.255"),
                    UDP_PORT);

            socket.send(broadcast);

            // Gui them tren loopback de kiem thu cung may
            DatagramPacket localhost = new DatagramPacket(
                    data,
                    data.length,
                    InetAddress.getByName("127.0.0.1"),
                    UDP_PORT);

            socket.send(localhost);

            System.out.println("Da gui DISCOVER_SERVICE qua UDP.");
            System.out.println("Dang tim Server trong "
                    + TIMEOUT_MS + " ms...");

            long deadline = System.currentTimeMillis() + TIMEOUT_MS;

            while (System.currentTimeMillis() < deadline) {

                int remaining = (int) (
                        deadline - System.currentTimeMillis());

                if (remaining <= 0) {
                    break;
                }

                socket.setSoTimeout(remaining);

                byte[] buffer = new byte[1024];

                DatagramPacket response =
                        new DatagramPacket(buffer, buffer.length);

                try {
                    socket.receive(response);
                } catch (java.net.SocketTimeoutException e) {
                    break;
                }

                String message = new String(
                        response.getData(),
                        response.getOffset(),
                        response.getLength(),
                        StandardCharsets.UTF_8).trim();

                String[] parts = message.split("\\s+");

                if (parts.length != 4
                        || !parts[0].equals("SERVICE")) {
                    continue;
                }

                try {
                    String name = parts[1];
                    int tcpPort = Integer.parseInt(parts[2]);
                    String version = parts[3];

                    if (tcpPort < 1 || tcpPort > 65535) {
                        continue;
                    }

                    InetAddress address = response.getAddress();

                    // Loai bo phan hoi trung theo IP va TCP port
                    String key = address.getHostAddress()
                            + ":" + tcpPort;

                    services.putIfAbsent(
                            key,
                            new ServiceInfo(
                                    name, tcpPort, version, address));

                } catch (NumberFormatException ignored) {
                }
            }

        } catch (Exception e) {
            System.out.println("Loi Discovery Client: "
                    + e.getMessage());
            return;
        }

        if (services.isEmpty()) {
            System.out.println("Khong tim thay Server nao.");
            System.out.println(
                    "Hay kiem tra Server, firewall va NetworkInterface.");
            return;
        }

        ArrayList<ServiceInfo> list =
                new ArrayList<>(services.values());

        System.out.println("\nDanh sach Server tim thay:");

        for (int i = 0; i < list.size(); i++) {
            ServiceInfo service = list.get(i);

            System.out.println(
                    (i + 1) + ". "
                    + service.name
                    + " | IP: " + service.address.getHostAddress()
                    + " | TCP Port: " + service.tcpPort
                    + " | Version: " + service.version);
        }

        try (Scanner sc = new Scanner(System.in)) {

            System.out.print(
                    "\nChon Server can ket noi (1-" + list.size()
                    + ", 0 de thoat): ");

            int choice;

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Lua chon khong hop le.");
                return;
            }

            if (choice == 0) {
                return;
            }

            if (choice < 1 || choice > list.size()) {
                System.out.println("Lua chon khong hop le.");
                return;
            }

            ServiceInfo selected = list.get(choice - 1);

            connectTcp(selected, sc);
        }
    }

    private static void connectTcp(
            ServiceInfo service, Scanner sc) {

        try (
            Socket socket = new Socket(
                    service.address, service.tcpPort);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream(),
                            StandardCharsets.UTF_8));

            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true)
        ) {

            socket.setSoTimeout(3000);

            System.out.println("\nDa ket noi TCP den "
                    + service.address.getHostAddress()
                    + ":" + service.tcpPort);

            System.out.println("Server: " + in.readLine());
            System.out.println("Nhap tin nhan, QUIT de thoat.");

            while (true) {
                System.out.print("> ");
                String message = sc.nextLine();

                out.println(message);

                String response = in.readLine();

                if (response == null) {
                    System.out.println("Server da dong ket noi.");
                    break;
                }

                System.out.println(response);

                if (message.equalsIgnoreCase("QUIT")) {
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Loi TCP: " + e.getMessage());
        }
    }
}
