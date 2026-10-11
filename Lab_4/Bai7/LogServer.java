
package Lab_4.Bai7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogServer {

    private static final int PORT = 7000;

    private static final ExecutorService pool =
            Executors.newFixedThreadPool(20);

    private static final Path LOG_DIR =
            Path.of("data", "logs");

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {

        try {
            Files.createDirectories(LOG_DIR);
        } catch (IOException e) {
            System.out.println("Khong tao duoc thu muc log.");
            return;
        }

        try (ServerSocket server = new ServerSocket(PORT)) {

            System.out.println("Log Server dang chay!");
            System.out.println("Port: " + PORT);
            System.out.println("Thu muc log: " + LOG_DIR.toAbsolutePath());

            while (true) {
                Socket socket = server.accept();
                pool.execute(() -> handleClient(socket));
            }

        } catch (IOException e) {
            System.out.println("Loi Server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void handleClient(Socket socket) {

        String clientId = null;

        try (
            Socket client = socket;

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            client.getInputStream(),
                            StandardCharsets.UTF_8));

            PrintWriter out = new PrintWriter(
                    client.getOutputStream(), true)
        ) {

            // Client phai gui HELLO clientId truoc
            String hello = in.readLine();

            if (hello == null || !hello.startsWith("HELLO ")) {
                out.println("ERR EXPECTED_HELLO");
                return;
            }

            clientId = hello.substring(6).trim();

            // Chi chap nhan chu, so, gach ngang va gach duoi
            if (!clientId.matches("[a-zA-Z0-9_-]+")) {
                out.println("ERR INVALID_CLIENT_ID");
                return;
            }

            Path logFile = LOG_DIR.resolve(clientId + ".txt");

            out.println("OK HELLO " + clientId);

            System.out.println(
                    clientId + " ket noi tu "
                    + socket.getRemoteSocketAddress());

            String message;

            while ((message = in.readLine()) != null) {

                if (message.equals("QUIT")) {
                    out.println("BYE");
                    break;
                }

                String timestamp =
                        LocalDateTime.now().format(FORMAT);

                String remote =
                        socket.getRemoteSocketAddress().toString();

                // Moi dong gom timestamp, remote address va noi dung
                String logLine = timestamp + " | "
                        + remote + " | " + message;

                Files.writeString(
                        logFile,
                        logLine + System.lineSeparator(),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);

                out.println("OK SAVED");
                System.out.println(logLine);
            }

        } catch (IOException e) {
            System.out.println(
                    "Client ngat ket noi: " + e.getMessage());
        }
    }
}
