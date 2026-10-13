package KiemTraTK1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private static final int PORT = 5000;

    private static final String[] ROOM_NAMES = {
        "Study", "Game", "TraiNghiem"
    };

    private static final ConcurrentHashMap<String, Set<ClientHandler>> rooms =
            new ConcurrentHashMap<>();

    public static void main(String[] args) {
        for (String room : ROOM_NAMES) {
            rooms.put(
                room,
                ConcurrentHashMap.newKeySet()
            );
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("=== CHAT SERVER ===");
            System.out.println("Server dang chay tai port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();

                System.out.println(
                    "Client moi ket noi: " + socket.getInetAddress()
                );

                ClientHandler client = new ClientHandler(socket);
                client.start();
            }

        } catch (IOException e) {
            System.out.println("Loi Server: " + e.getMessage());
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

    private static void broadcast(
            String room,
            String message,
            ClientHandler sender) {

        Set<ClientHandler> clients = rooms.get(room);

        if (clients != null) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    static class ClientHandler extends Thread {

        private final Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        private String username;
        private volatile String currentRoom;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        private void leaveRoom() {
            String room = currentRoom;

            if (room != null) {
                rooms.get(room).remove(this);
                currentRoom = null;

                broadcast(
                    room,
                    "INFO|" + username + " da roi phong.",
                    this
                );
            }
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
                );

                out = new PrintWriter(
                    socket.getOutputStream(), true
                );

                // Nhan ten nguoi dung
                username = in.readLine();

                if (username == null || username.trim().isEmpty()) {
                    return;
                }

                username = username.trim();

                System.out.println(username + " da dang nhap.");

                sendRooms(out);

                String message;

                while ((message = in.readLine()) != null) {

                    // Lenh JOIN|tenPhong
                    if (message.startsWith("JOIN|")) {
                        String requestedRoom =
                            message.substring(5).trim();

                        String room = findRoom(requestedRoom);

                        if (room == null) {
                            out.println("ERROR|Phong khong ton tai.");
                            sendRooms(out);
                            continue;
                        }

                        leaveRoom();

                        currentRoom = room;
                        rooms.get(room).add(this);

                        out.println(
                            "INFO|Ban da vao phong " + room
                        );

                        broadcast(
                            room,
                            "INFO|" + username + " da tham gia phong.",
                            this
                        );
                    }

                    // Gui tin nhan den phong hien tai
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
                            "CHAT|" + username + "|" + content,
                            this
                        );
                    }

                    // Roi phong va quay lai danh sach phong
                    else if (message.equals("/quit")) {
                        leaveRoom();
                        sendRooms(out);
                    }

                    // Ngat ket noi
                    else if (message.equals("/exit")) {
                        out.println("INFO|Da ngat ket noi.");
                        break;
                    }
                }

            } catch (IOException e) {
                System.out.println(
                    "Client " + username + " da mat ket noi."
                );

            } finally {
                leaveRoom();

                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Loi dong socket.");
                }

                System.out.println(
                    "Da dong ket noi: " + username
                );
            }
        }
    }
}
