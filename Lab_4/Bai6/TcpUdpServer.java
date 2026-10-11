
package Lab_4.Bai6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpUdpServer {

    private static final int TCP_PORT = 6000;
    private static final int UDP_PORT = 6001;

    private static final ExecutorService pool =
            Executors.newFixedThreadPool(10);

    public static void main(String[] args) {

        // Chay UDP Server tren thread rieng
        Thread udpThread = new Thread(() -> {
            try (DatagramSocket server = new DatagramSocket(UDP_PORT)) {

                System.out.println("UDP Server dang chay tren port "
                        + UDP_PORT);

                byte[] buffer = new byte[1024];

                while (true) {
                    DatagramPacket request =
                            new DatagramPacket(buffer, buffer.length);

                    server.receive(request);

                    DatagramPacket response = new DatagramPacket(
                            request.getData(),
                            request.getLength(),
                            request.getAddress(),
                            request.getPort());

                    server.send(response);
                }

            } catch (IOException e) {
                System.out.println("Loi UDP Server: " + e.getMessage());
            }
        });

        udpThread.start();

        // Chay TCP Server
        try (ServerSocket server = new ServerSocket(TCP_PORT)) {

            System.out.println("TCP Server dang chay tren port "
                    + TCP_PORT);

            while (true) {
                Socket socket = server.accept();
                pool.execute(() -> handleTcp(socket));
            }

        } catch (IOException e) {
            System.out.println("Loi TCP Server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void handleTcp(Socket socket) {

        try (
            Socket client = socket;

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            client.getInputStream(),
                            StandardCharsets.UTF_8));

            PrintWriter out = new PrintWriter(
                    client.getOutputStream(), true)
        ) {
            String message;

            while ((message = in.readLine()) != null) {
                out.println(message);
            }

        } catch (IOException e) {
            System.out.println("TCP Client da ngat ket noi.");
        }
    }
}
