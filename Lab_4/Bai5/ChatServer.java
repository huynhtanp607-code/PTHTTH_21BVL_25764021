
package Lab_4.Bai5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatServer {

    private static final int PORT = 5001;

    // Danh sach client dang ket noi, an toan khi nhieu thread truy cap
    private static final ConcurrentHashMap<String, ClientHandler> clients =
            new ConcurrentHashMap<>();

    // Thread pool xu ly nhieu client
    private static final ExecutorService pool =
            Executors.newFixedThreadPool(20);

    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(PORT)) {

            System.out.println("Chat Server dang chay!");
            System.out.println("Port: " + PORT);

            while (true) {
                Socket socket = server.accept();
                pool.execute(new ClientHandler(socket));
            }

        } catch (IOException e) {
            System.out.println("Loi Server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    // Gui tin nhan den cac client khac, khong gui lai nguoi gui
    private static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients.values()) {
            if (client != sender) {
                client.send(message);
            }
        }
    }

    // Xu ly tung client tren mot thread
    private static class ClientHandler implements Runnable {

        private final Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;
        private final AtomicBoolean closed = new AtomicBoolean(false);

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        private synchronized void send(String message) {
            if (out != null && !closed.get()) {
                out.println(message);
            }
        }

        @Override
        public void run() {

            try {
                in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream(), "UTF-8"));

                out = new PrintWriter(
                        socket.getOutputStream(), true);

                // Buoc 1: Nhan nickname
                String firstLine = in.readLine();

                if (firstLine == null || !firstLine.startsWith("NICK ")) {
                    send("ERR INVALID_FORMAT");
                    return;
                }

                String requestedName = firstLine.substring(5).trim();

                if (!requestedName.matches("[a-zA-Z0-9_]{1,20}")) {
                    send("ERR INVALID_NICKNAME");
                    return;
                }

                // Them nickname neu chua co nguoi su dung
                if (clients.putIfAbsent(requestedName, this) != null) {
                    send("ERR NICK_IN_USE");
                    return;
                }

                nickname = requestedName;
                send("OK NICK " + nickname);

                System.out.println(nickname + " da ket noi.");
                broadcast("[SERVER] " + nickname + " da tham gia chat.", this);

                // Buoc 2: Nhan va xu ly lenh
                String request;

                while ((request = in.readLine()) != null) {

                    if (request.equalsIgnoreCase("QUIT")) {
                        send("BYE");
                        break;
                    }

                    if (request.equalsIgnoreCase("USERS")) {
                        send("USERS " + String.join(", ", clients.keySet()));
                        continue;
                    }

                    if (request.startsWith("MSG ")) {
                        String message = request.substring(4).trim();

                        if (message.isEmpty()) {
                            send("ERR EMPTY_MESSAGE");
                        } else {
                            broadcast("[" + nickname + "] " + message, this);
                        }
                        continue;
                    }

                    send("ERR UNKNOWN_COMMAND");
                }

            } catch (IOException e) {
                System.out.println("Mat ket noi voi " + nickname);
            } finally {
                cleanup();
            }
        }

        // Xoa client khoi danh sach khi thoat hoac mat ket noi
        private void cleanup() {
            if (closed.compareAndSet(false, true)) {
                if (nickname != null &&
                        clients.remove(nickname, this)) {
                    broadcast("[SERVER] " + nickname + " da roi chat.", this);
                    System.out.println(nickname + " da ngat ket noi.");
                }

                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
