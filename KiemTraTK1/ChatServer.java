
package KiemTraTK1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.net.httpserver.HttpServer;

public class ChatServer {

    private static final int TCP_PORT = 5000;
    private static final int HTTP_PORT = 5001;

    private static final String[] ROOM_NAMES = {
        "Study", "Game", "TraiNghiem"
    };

    private static final ConcurrentHashMap<String, Set<ClientHandler>> rooms =
            new ConcurrentHashMap<>();

    private static final Set<ClientHandler> clients =
            ConcurrentHashMap.newKeySet();

    private static final AtomicInteger totalConnections =
            new AtomicInteger(0);

    public static void main(String[] args) {

        for (String room : ROOM_NAMES) {
            rooms.put(room, ConcurrentHashMap.newKeySet());
        }

        // Khoi dong HTTP Dashboard
        try {
            HttpServer httpServer = HttpServer.create(
                new InetSocketAddress(HTTP_PORT), 0
            );

            httpServer.createContext("/dashboard", exchange -> {
                StringBuilder data = new StringBuilder();

                data.append("========== CHAT SERVER DASHBOARD ==========\n");
                data.append("Trang thai Server: ONLINE\n");
                data.append("TCP Port: ").append(TCP_PORT).append("\n");
                data.append("HTTP Port: ").append(HTTP_PORT).append("\n");
                data.append("Thoi gian: ").append(
                    LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    )
                ).append("\n\n");

                data.append("Tong Client dang ket noi: ")
                    .append(clients.size()).append("\n");

                data.append("Tong luot ket noi: ")
                    .append(totalConnections.get()).append("\n\n");

                data.append("----- THONG KE PHONG CHAT -----\n");

                for (String room : ROOM_NAMES) {
                    Set<ClientHandler> members = rooms.get(room);

                    data.append("Phong ").append(room)
                        .append(": ")
                        .append(members.size())
                        .append(" nguoi\n");

                    for (ClientHandler member : members) {
                        data.append("  - ")
                            .append(member.username)
                            .append("\n");
                    }
                }

                data.append("\n----- DANH SACH CLIENT -----\n");

                for (ClientHandler client : clients) {
                    data.append("- ").append(client.username)
                        .append(" | Phong: ")
                        .append(
                            client.currentRoom == null
                                ? "Chua vao phong"
                                : client.currentRoom
                        )
                        .append("\n");
                }

                byte[] response = data.toString().getBytes(
                    StandardCharsets.UTF_8
                );

                exchange.getResponseHeaders().set(
                    "Content-Type", "text/plain; charset=UTF-8"
                );
                exchange.getResponseHeaders().set(
                    "Cache-Control", "no-store"
                );

                exchange.sendResponseHeaders(200, response.length);

                try (var body = exchange.getResponseBody()) {
                    body.write(response);
                }
            });

            httpServer.setExecutor(
                java.util.concurrent.Executors.newCachedThreadPool()
            );
            httpServer.start();

            System.out.println(
                "Dashboard HTTP: http://localhost:" + HTTP_PORT + "/dashboard"
            );

        } catch (IOException e) {
            System.out.println(
                "Khong khoi dong duoc Dashboard: " + e.getMessage()
            );
            return;
        }

        // Khoi dong TCP Chat Server
        try (ServerSocket serverSocket = new ServerSocket(TCP_PORT)) {

            System.out.println("========== CHAT SERVER ==========");
            System.out.println("TCP Server dang chay tai port " + TCP_PORT);
            System.out.println("Dang cho Client ket noi...");

            while (true) {
                Socket socket = serverSocket.accept();

                ClientHandler client = new ClientHandler(socket);
                client.start();
            }

        } catch (IOException e) {
            System.out.println("Loi TCP Server: " + e.getMessage());
        }
    }

    private static String findRoom(String name) {
        for (String room : ROOM_NAMES) {
            if (room.equalsIgnoreCase(name)) {
                return room;
            }
        }
        return null;
    }

    private static void sendRooms(PrintWriter out) {
        out.println("ROOMS|" + String.join(",", ROOM_NAMES));
    }

    private static void broadcast(String room, String message) {
        Set<ClientHandler> members = rooms.get(room);

        if (members != null) {
            for (ClientHandler client : members) {
                client.sendMessage(message);
            }
        }
    }

    static class ClientHandler extends Thread {

        private final Socket socket;

        private BufferedReader in;
        private PrintWriter out;

        private volatile String username = "Unknown";
        private volatile String currentRoom;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }

        private void leaveRoom() {
            String room = currentRoom;

            if (room != null) {
                currentRoom = null;
                rooms.get(room).remove(this);

                broadcast(
                    room,
                    "INFO|" + username + " da roi phong."
                );
            }
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(
                    new InputStreamReader(
                        socket.getInputStream(), StandardCharsets.UTF_8
                    )
                );

                out = new PrintWriter(
                    socket.getOutputStream(), true
                );

                String name = in.readLine();

                if (name == null || name.trim().isEmpty()
                        || name.contains("|")) {
                    return;
                }

                username = name.trim();

                clients.add(this);
                totalConnections.incrementAndGet();

                System.out.println(
                    username + " da ket noi tu " + socket.getInetAddress()
                );

                sendRooms(out);

                String message;

                while ((message = in.readLine()) != null) {

                    if (message.startsWith("JOIN|")) {
                        String requestedRoom = message.substring(5).trim();
                        String room = findRoom(requestedRoom);

                        if (room == null) {
                            out.println("ERROR|Phong khong ton tai.");
                            sendRooms(out);
                            continue;
                        }

                        leaveRoom();

                        currentRoom = room;
                        rooms.get(room).add(this);

                        out.println("INFO|Ban da vao phong " + room);

                        broadcast(
                            room,
                            "INFO|" + username + " da tham gia phong."
                        );
                    }

                    else if (message.startsWith("MSG|")) {
                        if (currentRoom == null) {
                            out.println(
                                "ERROR|Ban chua tham gia phong nao."
                            );
                            continue;
                        }

                        String content = message.substring(4);

                        broadcast(
                            currentRoom,
                            "CHAT|" + username + "|" + content
                        );
                    }

                    else if (message.equals("/quit")) {
                        leaveRoom();
                        sendRooms(out);
                    }

                    else if (message.equals("/exit")) {
                        out.println("INFO|Da ngat ket noi.");
                        break;
                    }
                }

            } catch (IOException e) {
                System.out.println(
                    "Mat ket noi voi Client: " + username
                );

            } finally {
                leaveRoom();
                clients.remove(this);

                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Loi dong socket.");
                }

                System.out.println(
                    "Da ngat ket noi: " + username
                );
            }
        }
    }
}
