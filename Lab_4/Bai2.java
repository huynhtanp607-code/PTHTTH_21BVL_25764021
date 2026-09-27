package Lab_4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Bai2 {

    private static final int PORT = 5000;

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Cách chạy:");
            System.out.println("Server: java -cp out Lab_4.Bai2 server");
            System.out.println("Client: java -cp out Lab_4.Bai2 client");
            return;
        }

        if (args[0].equalsIgnoreCase("server")) {
            startServer();
        } else if (args[0].equalsIgnoreCase("client")) {
            startClient();
        } else {
            System.out.println("Tham số không hợp lệ.");
            System.out.println("Dùng: server hoặc client");
        }
    }

    // =========================
    // SERVER
    // =========================

    private static void startServer() {

        try (ServerSocket server = new ServerSocket(PORT)) {

            System.out.println(
                "TCP Server đang chạy tại port " + PORT
            );

            while (true) {

                try (Socket socket = server.accept()) {

                    System.out.println("Client đã kết nối.");

                    BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                            socket.getInputStream(),
                            StandardCharsets.UTF_8
                        )
                    );

                    PrintWriter out = new PrintWriter(
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

                        String response = process(request);

                        out.println(response);

                        if (request.equals("QUIT")) {
                            break;
                        }
                    }

                } catch (IOException e) {

                    System.err.println(
                        "Lỗi client: " + e.getMessage()
                    );
                }
            }

        } catch (IOException e) {

            System.err.println(
                "Không mở được server: " + e.getMessage()
            );
        }
    }

    // =========================
    // XỬ LÝ DỮ LIỆU
    // =========================

    private static String process(String request) {

        if (request.equals("QUIT")) {
            return "OK BYE";
        }

        if (request.length() == 1) {

            char c = request.charAt(0);

            switch (c) {

                case '0':
                    return "OK KHÔNG";

                case '1':
                    return "OK MỘT";

                case '2':
                    return "OK HAI";

                case '3':
                    return "OK BA";

                case '4':
                    return "OK BỐN";

                case '5':
                    return "OK NĂM";

                case '6':
                    return "OK SÁU";

                case '7':
                    return "OK BẢY";

                case '8':
                    return "OK TÁM";

                case '9':
                    return "OK CHÍN";

                default:
                    return "ERR INVALID_DIGIT";
            }
        }

        return "ERR INVALID_DIGIT";
    }

    // =========================
    // CLIENT
    // =========================

    private static void startClient() {

        try (
            Socket socket = new Socket("localhost", PORT);

            BufferedReader console = new BufferedReader(
                new InputStreamReader(
                    System.in,
                    StandardCharsets.UTF_8
                )
            );

            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream(),
                    StandardCharsets.UTF_8
                )
            );

            PrintWriter out = new PrintWriter(
                new OutputStreamWriter(
                    socket.getOutputStream(),
                    StandardCharsets.UTF_8
                ),
                true
            )
        ) {

            System.out.println(
                "Đã kết nối Server."
            );

            System.out.println(
                "Nhập số từ 0-9 hoặc QUIT:"
            );

            String request;

            while ((request = console.readLine()) != null) {

                // Gửi dữ liệu cho server
                out.println(request);

                // Nhận kết quả
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

                if (request.equals("QUIT")) {
                    break;
                }
            }

        } catch (IOException e) {

            System.err.println(
                "Lỗi kết nối: " + e.getMessage()
            );
        }
    }
}