
package Lab_4.Bai6;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class TcpUdpClient {

    private static final String HOST = "localhost";
    private static final int TCP_PORT = 6000;
    private static final int UDP_PORT = 6001;

    private static final int TOTAL_MESSAGES = 1000;
    private static final int MESSAGE_SIZE = 128;
    private static final int REPEAT = 5;
    private static final int UDP_TIMEOUT = 200;

    public static void main(String[] args) throws Exception {

        System.out.println("SO SANH TCP VA UDP");
        System.out.println("So thong diep: " + TOTAL_MESSAGES);
        System.out.println("Kich thuoc: " + MESSAGE_SIZE + " byte");
        System.out.println("So lan lap: " + REPEAT);
        System.out.println("UDP timeout: " + UDP_TIMEOUT + " ms");
        System.out.println("--------------------------------------");

        for (int run = 1; run <= REPEAT; run++) {

            System.out.println("\nLAN CHAY " + run);

            testTCP(run);
            testUDP(run);
        }
    }

    // Tao thong diep co kich thuoc co dinh 128 byte
    private static byte[] createMessage(int run, int index) {

        String prefix = "RUN:" + run + ":MSG:" + index;

        StringBuilder message = new StringBuilder(prefix);

        while (message.length() < MESSAGE_SIZE) {
            message.append(' ');
        }

        return message.toString().getBytes(StandardCharsets.US_ASCII);
    }

    // Do TCP
    private static void testTCP(int run) {

        int received = 0;
        long start = System.nanoTime();

        try (
            Socket socket = new Socket(HOST, TCP_PORT);

            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream(),
                    StandardCharsets.US_ASCII));

            PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true)
        ) {
            socket.setSoTimeout(2000);

            for (int i = 1; i <= TOTAL_MESSAGES; i++) {

                byte[] data = createMessage(run, i);
                String message = new String(
                    data, StandardCharsets.US_ASCII);

                out.println(message);

                String response = in.readLine();

                if (response != null && response.equals(message)) {
                    received++;
                }
            }

        } catch (Exception e) {
            System.out.println("Loi TCP: " + e.getMessage());
        }

        long end = System.nanoTime();
        double elapsedMs = (end - start) / 1_000_000.0;

        System.out.printf(
            "TCP | Lan %d | Thoi gian: %.3f ms | "
            + "Phan hoi: %d/%d%n",
            run, elapsedMs, received, TOTAL_MESSAGES);
    }

    // Do UDP
    private static void testUDP(int run) {

        int received = 0;
        long start = System.nanoTime();

        try (DatagramSocket socket = new DatagramSocket()) {

            socket.setSoTimeout(UDP_TIMEOUT);

            InetAddress address = InetAddress.getByName(HOST);

            for (int i = 1; i <= TOTAL_MESSAGES; i++) {

                byte[] data = createMessage(run, i);

                DatagramPacket request = new DatagramPacket(
                    data, data.length, address, UDP_PORT);

                socket.send(request);

                byte[] buffer = new byte[MESSAGE_SIZE];

                DatagramPacket response = new DatagramPacket(
                    buffer, buffer.length);

                try {
                    socket.receive(response);

                    if (response.getLength() == MESSAGE_SIZE
                            && response.getPort() == UDP_PORT
                            && response.getAddress().equals(address)
                            && equalsBytes(data, response)) {
                        received++;
                    }

                } catch (SocketTimeoutException e) {
                    // Khong nhan duoc phan hoi trong thoi gian cho
                }
            }

        } catch (Exception e) {
            System.out.println("Loi UDP: " + e.getMessage());
        }

        long end = System.nanoTime();
        double elapsedMs = (end - start) / 1_000_000.0;

        System.out.printf(
            "UDP | Lan %d | Thoi gian: %.3f ms | "
            + "Phan hoi: %d/%d%n",
            run, elapsedMs, received, TOTAL_MESSAGES);
    }

    private static boolean equalsBytes(
            byte[] sent, DatagramPacket received) {

        if (sent.length != received.getLength()) {
            return false;
        }

        for (int i = 0; i < sent.length; i++) {
            if (sent[i] != received.getData()[i]) {
                return false;
            }
        }

        return true;
    }
}
