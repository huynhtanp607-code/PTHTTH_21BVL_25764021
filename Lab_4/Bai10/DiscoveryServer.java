
package Lab_4.Bai10;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiscoveryServer {

    private static final int UDP_PORT = 9001;
    private static final int TCP_PORT = 9002;

    private static final String SERVICE_NAME = "ChatService";
    private static final String VERSION = "1.0";

    private static final ExecutorService pool =
            Executors.newFixedThreadPool(10);

    public static void main(String[] args) {

        // Khoi dong UDP Discovery
        Thread udpThread = new Thread(
                DiscoveryServer::runUdpServer);

        udpThread.start();

        // Khoi dong TCP Service
        runTcpServer();
    }

    private static void runUdpServer() {

        try (DatagramSocket socket = new DatagramSocket(UDP_PORT)) {

            socket.setBroadcast(true);

            System.out.println(
                    "UDP Discovery dang nghe port " + UDP_PORT);

            byte[] buffer = new byte[1024];

            while (true) {

                DatagramPacket request =
                        new DatagramPacket(buffer, buffer.length);

                socket.receive(request);

                String message = new String(
                        request.getData(),
                        request.getOffset(),
                        request.getLength(),
                        StandardCharsets.UTF_8).trim();

                if (message.equals("DISCOVER_SERVICE")) {

                    String response = "SERVICE "
                            + SERVICE_NAME + " "
                            + TCP_PORT + " "
                            + VERSION;

                    byte[] data = response.getBytes(
                            StandardCharsets.UTF_8);

                    DatagramPacket reply = new DatagramPacket(
                            data,
                            data.length,
                            request.getAddress(),
                            request.getPort());

                    socket.send(reply);

                    System.out.println(
                            "Da phan hoi discovery cho "
                            + request.getAddress().getHostAddress());
                }
            }

        } catch (Exception e) {
            System.out.println(
                    "Loi UDP Server: " + e.getMessage());
        }
    }

    private static void runTcpServer() {

        try (ServerSocket server = new ServerSocket(TCP_PORT)) {

            System.out.println(
                    "TCP Service dang chay port " + TCP_PORT);

            while (true) {
                Socket client = server.accept();

                pool.execute(() -> handleClient(client));
            }

        } catch (Exception e) {
            System.out.println(
                    "Loi TCP Service: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void handleClient(Socket socket) {

        try (
            Socket client = socket;

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            client.getInputStream(),
                            StandardCharsets.UTF_8));

            PrintWriter out = new PrintWriter(
                    client.getOutputStream(), true)
        ) {

            out.println("OK CONNECTED " + SERVICE_NAME
                    + " VERSION " + VERSION);

            String message;

            while ((message = in.readLine()) != null) {

                if (message.equalsIgnoreCase("QUIT")) {
                    out.println("BYE");
                    break;
                }

                out.println("SERVER RECEIVED: " + message);
            }

        } catch (Exception e) {
            System.out.println("TCP Client da ngat ket noi.");
        }
    }
}
