package Lab_4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Bai3 {

    private static final int TCP_PORT = 5000;
    private static final int UDP_PORT = 5001;

    // Định dạng ngày: dd MM yyyy
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MM yyyy");

    // Định dạng giờ: HH mm ss
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH mm ss");

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Cách chạy:");
            System.out.println("TCP Server : java -cp out Lab_4.Bai3 tcp-server");
            System.out.println("TCP Client : java -cp out Lab_4.Bai3 tcp-client");
            System.out.println("UDP Server : java -cp out Lab_4.Bai3 udp-server");
            System.out.println("UDP Client : java -cp out Lab_4.Bai3 udp-client");
            return;
        }

        switch (args[0].toLowerCase()) {

            case "tcp-server":
                startTcpServer();
                break;

            case "tcp-client":
                startTcpClient();
                break;

            case "udp-server":
                startUdpServer();
                break;

            case "udp-client":
                startUdpClient();
                break;

            default:
                System.out.println("Tham số không hợp lệ.");
        }
    }

    // =========================
    // XỬ LÝ LỆNH NGÀY GIỜ
    // =========================

    private static String processCommand(String command) {

        String cmd = command.trim().toUpperCase();

        LocalDateTime now = LocalDateTime.now();

        if (cmd.equals("DATE")) {
            return "OK " + now.format(DATE_FORMAT);
        }

        if (cmd.equals("TIME")) {
            return "OK " + now.format(TIME_FORMAT);
        }

        if (cmd.equals("DATETIME")) {
            return "OK " + now.format(DATE_FORMAT)
                    + " " + now.format(TIME_FORMAT);
        }

        if (cmd.equals("QUIT")) {
            return "OK BYE";
        }

        return "ERR UNKNOWN_COMMAND";
    }

    // =========================
    // TCP SERVER
    // =========================

    private static void startTcpServer() {

        try (ServerSocket server = new ServerSocket(TCP_PORT)) {

            System.out.println(
                    "TCP Server đang chạy tại port " + TCP_PORT
            );

            while (true) {

                try (Socket socket = server.accept()) {

                    System.out.println("TCP Client đã kết nối.");

                    BufferedReader in =
                            new BufferedReader(
                                    new InputStreamReader(
                                            socket.getInputStream(),
                                            StandardCharsets.UTF_8
                                    )
                            );

                    PrintWriter out =
                            new PrintWriter(
                                    new OutputStreamWriter(
                                            socket.getOutputStream(),
                                            StandardCharsets.UTF_8
                                    ),
                                    true
                            );

                    String request;

                    while ((request = in.readLine()) != null) {

                        System.out.println(
                                "Client gửi: [" + request + "]"
                        );

                        String response =
                                processCommand(request);

                        out.println(response);

                        if (request.trim().equalsIgnoreCase("QUIT")) {
                            break;
                        }
                    }

                } catch (IOException e) {

                    System.err.println(
                            "Lỗi phiên TCP: " + e.getMessage()
                    );
                }
            }

        } catch (IOException e) {

            System.err.println(
                    "Không mở được TCP Server: " + e.getMessage()
            );
        }
    }

    // =========================
    // TCP CLIENT
    // =========================

    private static void startTcpClient() {

        try (
                Socket socket =
                        new Socket("localhost", TCP_PORT);

                BufferedReader console =
                        new BufferedReader(
                                new InputStreamReader(
                                        System.in,
                                        StandardCharsets.UTF_8
                                )
                        );

                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream(),
                                        StandardCharsets.UTF_8
                                )
                        );

                PrintWriter out =
                        new PrintWriter(
                                new OutputStreamWriter(
                                        socket.getOutputStream(),
                                        StandardCharsets.UTF_8
                                ),
                                true
                        )
        ) {

            System.out.println("Đã kết nối TCP Server.");
            System.out.println(
                    "Nhập DATE, TIME, DATETIME hoặc QUIT:"
            );

            String request;

            while ((request = console.readLine()) != null) {

                out.println(request);

                String response = in.readLine();

                if (response == null) {

                    System.out.println(
                            "Server đã đóng kết nối."
                    );

                    break;
                }

                System.out.println(
                        "Server: " + response
                );

                if (request.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }

        } catch (IOException e) {

            System.err.println(
                    "Lỗi TCP Client: " + e.getMessage()
            );
        }
    }

    // =========================
    // UDP SERVER
    // =========================

    private static void startUdpServer() {

        try (DatagramSocket socket =
                     new DatagramSocket(UDP_PORT)) {

            System.out.println(
                    "UDP Server đang chạy tại port " + UDP_PORT
            );

            byte[] buffer = new byte[4096];

            while (true) {

                DatagramPacket request =
                        new DatagramPacket(
                                buffer,
                                buffer.length
                        );

                socket.receive(request);

                String command =
                        new String(
                                request.getData(),
                                request.getOffset(),
                                request.getLength(),
                                StandardCharsets.UTF_8
                        );

                System.out.println(
                        "Client UDP gửi: [" + command + "]"
                );

                String response =
                        processCommand(command);

                byte[] responseData =
                        response.getBytes(
                                StandardCharsets.UTF_8
                        );

                DatagramPacket reply =
                        new DatagramPacket(
                                responseData,
                                responseData.length,
                                request.getAddress(),
                                request.getPort()
                        );

                socket.send(reply);
            }

        } catch (IOException e) {

            System.err.println(
                    "Lỗi UDP Server: " + e.getMessage()
            );
        }
    }

    // =========================
    // UDP CLIENT
    // =========================

    private static void startUdpClient() {

        try (
                DatagramSocket socket =
                        new DatagramSocket();

                BufferedReader console =
                        new BufferedReader(
                                new InputStreamReader(
                                        System.in,
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {

            socket.setSoTimeout(3000);

            InetAddress address =
                    InetAddress.getByName("localhost");

            System.out.println("Đã kết nối UDP Server.");
            System.out.println(
                    "Nhập DATE, TIME hoặc DATETIME:"
            );
            System.out.println(
                    "UDP không cần lệnh QUIT."
            );

            while (true) {

                System.out.print("> ");

                String command =
                        console.readLine();

                if (command == null) {
                    break;
                }

                // UDP không cần QUIT
                if (command.trim().equalsIgnoreCase("QUIT")) {

                    System.out.println(
                            "UDP không sử dụng lệnh QUIT."
                    );

                    break;
                }

                byte[] data =
                        command.getBytes(
                                StandardCharsets.UTF_8
                        );

                DatagramPacket request =
                        new DatagramPacket(
                                data,
                                data.length,
                                address,
                                UDP_PORT
                        );

                socket.send(request);

                byte[] buffer =
                        new byte[4096];

                DatagramPacket response =
                        new DatagramPacket(
                                buffer,
                                buffer.length
                        );

                try {

                    socket.receive(response);

                    String result =
                            new String(
                                    response.getData(),
                                    response.getOffset(),
                                    response.getLength(),
                                    StandardCharsets.UTF_8
                            );

                    System.out.println(
                            "Server: " + result
                    );

                } catch (java.net.SocketTimeoutException e) {

                    System.out.println(
                            "Timeout: Không nhận được phản hồi từ UDP Server."
                    );
                }
            }

        } catch (IOException e) {

            System.err.println(
                    "Lỗi UDP Client: " + e.getMessage()
            );
        }
    }
}